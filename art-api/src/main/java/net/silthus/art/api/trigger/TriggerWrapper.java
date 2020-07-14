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
import net.silthus.art.ActionContext;
import net.silthus.art.Scheduler;
import net.silthus.art.Storage;
import net.silthus.art.Target;
import net.silthus.art.api.AbstractArtObjectContext;
import net.silthus.art.impl.DefaultActionContext;
import net.silthus.art.ActionHolder;
import net.silthus.art.api.requirements.RequirementWrapper;
import net.silthus.art.api.requirements.RequirementHolder;

import java.util.*;
import java.util.function.Predicate;

import static net.silthus.art.api.storage.StorageConstants.LAST_EXECUTION;
import static net.silthus.art.util.ReflectionUtil.getEntryForTarget;

public class TriggerWrapper<TConfig> extends AbstractArtObjectContext<Object, TConfig, TriggerConfig<TConfig>> implements ActionHolder, RequirementHolder {

    private final Map<Class<?>, Set<TriggerListener<?>>> listeners = new HashMap<>();
    @Getter
    private final List<ActionContext<?>> actions = new ArrayList<>();
    @Getter
    private final List<RequirementWrapper<?, ?>> requirements = new ArrayList<>();
    private final Scheduler scheduler;

    public TriggerWrapper(TriggerConfig<TConfig> config, Scheduler scheduler, Storage storage) {
        super(storage, Object.class, config);
        this.scheduler = scheduler;
    }

    private Optional<Scheduler> getScheduler() {
        return Optional.ofNullable(scheduler);
    }

    @Override
    public void addAction(ActionContext<?> action) {
        this.actions.add(action);
    }

    @Override
    public void addRequirement(RequirementWrapper<?, ?> requirement) {
        this.requirements.add(requirement);
    }

    /**
     * Registers the given {@link TriggerListener} to listen for events
     * fired by this trigger.
     *
     * @param listener trigger listener to register
     * @see Set#add(Object)
     */
    <TTarget> void addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
        if (!listeners.containsKey(targetClass)) {
            listeners.put(targetClass, new HashSet<>());
        }
        listeners.get(targetClass).add(listener);
    }

    /**
     * Unregisters the given listener from listening to this trigger.
     *
     * @param listener trigger listener to unregister
     */
    <TTarget> void removeListener(TriggerListener<TTarget> listener) {
        listeners.values().forEach(triggerListeners -> triggerListeners.remove(listener));
    }

    /**
     * Fires this trigger and informs all listeners about its executing
     * if the given predicate and target type matches.
     *
     * @param target    target that fired the trigger
     * @param predicate predicate to check before informing all listeners
     * @param <TTarget> target type
     */
    @SuppressWarnings("unchecked")
    <TTarget> void trigger(Target<TTarget> target, Predicate<TriggerWrapper<TConfig>> predicate) {

        if (cannotExecute(target)) return;

        Runnable runnable = () -> {
            if (predicate.test(this) && testRequirements(target)) {

                getStorage().data(this, target, LAST_EXECUTION, System.currentTimeMillis());

                if (getOptions().isExecuteActions()) executeActions(target);

                getEntryForTarget(target.getSource(), listeners).orElse(new HashSet<>()).stream()
                        .map(triggerListener -> (TriggerListener<TTarget>) triggerListener)
                        .forEach(listener -> listener.onTrigger(target));
            }
        };

        long delay = getOptions().getDelay();
        if (getScheduler().isPresent() && delay > 0) {
            getScheduler().get().runTaskLater(runnable, delay);
        } else {
            runnable.run();
        }
    }

    /**
     * Checks if the {@link DefaultActionContext} has the execute_once option
     * and already executed once for the {@link Target}.
     *
     * @param target target to check
     * @param <TTarget> target type
     * @return true if action was already executed and should only execute once
     */
    public <TTarget> boolean wasExecutedOnce(Target<TTarget> target) {

        return getOptions().isExecuteOnce() && getLastExecution(target) > 0;
    }

    /**
     * Checks if the action is on cooldown for the given {@link Target}.
     * Will always return false if no cooldown is defined (set to zero).
     *
     * @param target target to check
     * @param <TTarget> target type
     * @return true if action is on cooldown
     */
    public <TTarget> boolean isOnCooldown(Target<TTarget> target) {
        long cooldown = getOptions().getCooldown();
        if (cooldown < 1) return false;

        long lastExecution = getLastExecution(target);

        if (lastExecution < 1) return false;

        return System.currentTimeMillis() < lastExecution + cooldown;
    }

    private <TTarget> boolean cannotExecute(Target<TTarget> target) {
        return wasExecutedOnce(target) || isOnCooldown(target);
    }

    private <TTarget> long getLastExecution(Target<TTarget> target) {
        return getStorage().get(this, target, LAST_EXECUTION, Long.class).orElse(0L);
    }
}
