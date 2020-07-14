/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.silthus.art.api.trigger;

import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.api.AbstractArtObjectContext;
import net.silthus.art.conf.TriggerConfig;
import net.silthus.art.impl.DefaultActionContext;
import net.silthus.art.impl.DefaultRequirementContext;

import java.util.*;
import java.util.function.Predicate;

import static net.silthus.art.api.storage.StorageConstants.LAST_EXECUTION;
import static net.silthus.art.util.ReflectionUtil.getEntryForTarget;

public class DefaultTriggerContext extends AbstractArtObjectContext implements TriggerContext {

    @Getter
    private final List<ActionContext<?>> actions = new ArrayList<>();
    @Getter
    private final List<RequirementContext<?>> requirements = new ArrayList<>();
    private final Map<Class<?>, Set<TriggerListener<?>>> listeners = new HashMap<>();
    @Getter
    private final TriggerConfig config;

    public DefaultTriggerContext(@NonNull Configuration configuration, @NonNull String uniqueId, @NonNull Class<?> targetClass, @NonNull TriggerConfig config) {
        super(configuration, uniqueId, targetClass);
        this.config = config;
    }

    @Override
    public void addAction(ActionContext<?> action) {
        this.actions.add(action);
    }

    @Override
    public void addRequirement(DefaultRequirementContext<?> requirement) {
        this.requirements.add(requirement);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> void trigger(ExecutionContext<TTarget, TriggerContext> context, Predicate<ExecutionContext<TTarget, TriggerContext>> predicate) {

        Target<TTarget> target = context.target();

        if (cannotExecute(target)) return;

        Runnable runnable = () -> {
            if (predicate.test(context) && testRequirements(context)) {

                store(target, LAST_EXECUTION, System.currentTimeMillis());

                if (getConfig().isExecuteActions()) executeActions(target);

                getEntryForTarget(target.getSource(), listeners).orElse(new HashSet<>()).stream()
                        .map(triggerListener -> (TriggerListener<TTarget>) triggerListener)
                        .forEach(listener -> listener.onTrigger(target));
            }
        };

        long delay = getConfig().getDelay();
        if (configuration().scheduler().isPresent() && delay > 0) {
            configuration().scheduler().get().runTaskLater(runnable, delay);
        } else {
            runnable.run();
        }
    }

    private <TTarget> boolean cannotExecute(Target<TTarget> target) {
        return wasExecutedOnce(target) || isOnCooldown(target);
    }

    @Override
    public <TTarget> void addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
        if (!listeners.containsKey(targetClass)) {
            listeners.put(targetClass, new HashSet<>());
        }
        listeners.get(targetClass).add(listener);
    }

    @Override
    public <TTarget> void removeListener(TriggerListener<TTarget> listener) {
        listeners.values().forEach(triggerListeners -> triggerListeners.remove(listener));
    }

    /**
     * Checks if the {@link DefaultActionContext} has the execute_once option
     * and already executed once for the {@link Target}.
     *
     * @param target target to check
     * @param <TTarget> target type
     * @return true if action was already executed and should only execute once
     */
    private <TTarget> boolean wasExecutedOnce(Target<TTarget> target) {

        return getConfig().isExecuteOnce() && getLastExecution(target) > 0;
    }

    /**
     * Checks if the action is on cooldown for the given {@link Target}.
     * Will always return false if no cooldown is defined (set to zero).
     *
     * @param target target to check
     * @param <TTarget> target type
     * @return true if action is on cooldown
     */
    private <TTarget> boolean isOnCooldown(Target<TTarget> target) {
        long cooldown = getConfig().getCooldown();
        if (cooldown < 1) return false;

        long lastExecution = getLastExecution(target);

        if (lastExecution < 1) return false;

        return System.currentTimeMillis() < lastExecution + cooldown;
    }

    private <TTarget> long getLastExecution(Target<TTarget> target) {
        return store(target, LAST_EXECUTION, Long.class).orElse(0L);
    }
}
