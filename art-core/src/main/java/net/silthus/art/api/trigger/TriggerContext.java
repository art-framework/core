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
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.actions.ActionHolder;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.requirements.RequirementHolder;
import net.silthus.art.api.scheduler.Scheduler;
import net.silthus.art.api.storage.StorageProvider;

import java.util.*;
import java.util.function.Predicate;

import static net.silthus.art.api.storage.StorageConstants.LAST_EXECUTION;
import static net.silthus.art.util.ReflectionUtil.getEntryForTarget;

public class TriggerContext<TConfig> extends ArtContext<Object, TConfig, TriggerConfig<TConfig>> implements ActionHolder, RequirementHolder {

    private final Map<Class<?>, Set<TriggerListener<?>>> listeners = new HashMap<>();
    @Getter
    private final List<ActionContext<?, ?>> actions = new ArrayList<>();
    @Getter
    private final List<RequirementContext<?, ?>> requirements = new ArrayList<>();
    private final Scheduler scheduler;

    public TriggerContext(TriggerConfig<TConfig> config, Scheduler scheduler, StorageProvider storageProvider) {
        super(storageProvider, Object.class, config);
        this.scheduler = scheduler;
    }

    private Optional<Scheduler> getScheduler() {
        return Optional.ofNullable(scheduler);
    }

    @Override
    public void addAction(ActionContext<?, ?> action) {
        this.actions.add(action);
    }

    @Override
    public void addRequirement(RequirementContext<?, ?> requirement) {
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
    <TTarget> void trigger(Target<TTarget> target, Predicate<TriggerContext<TConfig>> predicate) {

        if (cannotExecute(target)) return;

        Runnable runnable = () -> {
            if (predicate.test(this) && testRequirements(target)) {

                getStorageProvider().store(this, target, LAST_EXECUTION, System.currentTimeMillis());

                executeActions(target);

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
     * Checks if the {@link ActionContext} has the execute_once option
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
        Long storedLong = getStorageProvider().get(this, target, LAST_EXECUTION, Long.class);
        if (storedLong == null) return 0;
        return storedLong;
    }
}
