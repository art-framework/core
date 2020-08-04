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

package io.artframework.impl;

import io.artframework.*;
import io.artframework.conf.Constants;
import io.artframework.conf.TriggerConfig;
import io.artframework.events.EventHandler;
import io.artframework.events.EventListener;
import io.artframework.events.EventPriority;
import io.artframework.events.TriggerEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.*;

@SuppressWarnings("unused")
@Accessors(fluent = true)
public class DefaultTriggerContext extends AbstractArtObjectContext<Trigger> implements TriggerContext, EventListener {

    @Getter
    private final List<ActionContext<?>> actions = new ArrayList<>();
    @Getter
    private final List<RequirementContext<?>> requirements = new ArrayList<>();
    private final Map<Class<?>, Set<TriggerListener<?>>> listeners = new HashMap<>();
    @Getter
    private final TriggerConfig config;

    public DefaultTriggerContext(
            @NonNull Configuration configuration,
            @NonNull ArtObjectMeta<Trigger> information,
            @NonNull TriggerConfig config
    ) {
        super(configuration, information);
        this.config = config;

        configuration().events().register(this);
    }

    @Override
    public @NonNull String uniqueId() {
        return this.config().identifier();
    }

    @Override
    public void addAction(ActionContext<?> action) {
        this.actions.add(action);
    }

    @Override
    public void addRequirement(RequirementContext<?> requirement) {
        this.requirements.add(requirement);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTriggerEvent(TriggerEvent event) {
        if (!event.getIdentifier().equalsIgnoreCase(meta().identifier())) return;

        ExecutionContext<?> executionContext = ExecutionContext.of(
                configuration(),
                this,
                Arrays.stream(event.getTargets()).map(TriggerTarget::target).toArray(Target[]::new)
        );

        trigger(event.getTargets(), executionContext.next(this));
    }

    @Override
    public void trigger(final TriggerTarget<?>[] targets, final ExecutionContext<TriggerContext> context) {

        Runnable runnable = () -> {
            for (TriggerTarget<?> target : targets) {
                if (cannotExecute(target.target())) continue;

                if (target instanceof ConfiguredTriggerTarget) {
                    Result result = testTrigger((ConfiguredTriggerTarget<?, ?>) target, context).combine();
                    if (result.failure()) return;
                }

                if (testRequirements(context).success()) {

                    store(target.target(), Constants.Storage.LAST_EXECUTION, System.currentTimeMillis());

                    if (this.config().executeActions()) executeActions(target.target(), context);
                }
            }

            callListeners(context);
        };

        long delay = this.config().delay();
        if (configuration().scheduler().isPresent() && delay > 0) {
            configuration().scheduler().get().runTaskLater(runnable, delay);
        } else {
            runnable.run();
        }
    }

    private <TTarget, TConfig> Result testTrigger(ConfiguredTriggerTarget<TTarget, TConfig> target, ExecutionContext<TriggerContext> executionContext) {
        try {
            TConfig config = target.configMap().applyTo(target.configClass().newInstance());
            return target.test(executionContext, config);
        } catch (InstantiationException | IllegalAccessException e) {
            return Result.error(e, "Failed to create a new config instance from the config class " + target.configClass().getSimpleName() + ". " +
                    "Make sure the class is public and has an public parameterless constructor.");
        }
    }

    @SuppressWarnings("unchecked")
    private <TTarget> void callListeners(ExecutionContext<TriggerContext> executionContext) {
        for (Map.Entry<Class<?>, Set<TriggerListener<?>>> entry : listeners.entrySet()) {
            Target<TTarget>[] targets = Arrays.stream(executionContext.getTargets())
                    .filter(target -> entry.getKey().isInstance(target.source()))
                    .toArray(Target[]::new);

            entry.getValue().stream()
                    .map(listener -> (TriggerListener<TTarget>) listener)
                    .forEach(listener -> listener.onTrigger(targets, executionContext));
        }
    }

    @Override
    public <TTarget> void addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
        if (!listeners.containsKey(targetClass)) {
            listeners.put(targetClass, new HashSet<>());
        }
        listeners.get(targetClass).add(listener);
    }

    @Override
    public void addListener(TriggerListener<Object> listener) {
        addListener(Object.class, listener);
    }

    @Override
    public <TTarget> void removeListener(TriggerListener<TTarget> listener) {
        listeners.values().forEach(triggerListeners -> triggerListeners.remove(listener));
    }

    @Override
    public void close() {
        configuration().events().unregister(this);
    }

    private <TTarget> boolean cannotExecute(Target<TTarget> target) {
        return wasExecutedOnce(target) || isOnCooldown(target);
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

        return this.config().executeOnce() && getLastExecution(target) > 0;
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
        long cooldown = this.config().cooldown();
        if (cooldown < 1) return false;

        long lastExecution = getLastExecution(target);

        if (lastExecution < 1) return false;

        return System.currentTimeMillis() < lastExecution + cooldown;
    }

    private <TTarget> long getLastExecution(Target<TTarget> target) {
        return store(target, Constants.Storage.LAST_EXECUTION, Long.class).orElse(0L);
    }
}
