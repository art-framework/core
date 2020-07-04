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

package net.silthus.art.api.actions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.api.Action;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.requirements.RequirementHolder;
import net.silthus.art.api.scheduler.Scheduler;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.api.trigger.Target;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.silthus.art.api.storage.StorageConstants.LAST_EXECUTION;

/**
 * The action context is created for every unique {@link Action} configuration.
 * It holds all relevant information to execute the action and tracks the dependencies.
 *
 * @param <TTarget> target type of the action
 * @param <TConfig> config type of the action
 */
public final class ActionContext<TTarget, TConfig> extends ArtContext<TTarget, TConfig, ActionConfig<TConfig>> implements Action<TTarget, TConfig>, RequirementHolder, ActionHolder {

    @Getter(AccessLevel.PROTECTED)
    private final Action<TTarget, TConfig> action;
    @Getter
    private final List<ActionContext<?, ?>> actions = new ArrayList<>();
    @Getter
    private final List<RequirementContext<?, ?>> requirements = new ArrayList<>();
    private final Scheduler scheduler;

    public ActionContext(Class<TTarget> tTargetClass, Action<TTarget, TConfig> action, ActionConfig<TConfig> config, @Nullable Scheduler scheduler, StorageProvider storageProvider) {
        super(storageProvider, tTargetClass, config);
        this.scheduler = scheduler;
        Objects.requireNonNull(action);
        this.action = action;
    }

    private Optional<Scheduler> getScheduler() {
        return Optional.ofNullable(scheduler);
    }

    @Override
    public void addAction(ActionContext<?, ?> action) {
        this.actions.add(action);
    }

    @Override
    public final void addRequirement(RequirementContext<?, ?> requirement) {
        this.requirements.add(requirement);
    }

    public final void execute(@NonNull Target<TTarget> target) {

        execute(target, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void execute(@NonNull Target<TTarget> target, ActionContext<TTarget, TConfig> context) {

        if (context != null && context != this)
            throw new UnsupportedOperationException("ActionContext#execute(target, context) must not be called directly. Use ActionResult#execute(target) instead.");

        if (!isTargetType(target)) return;
        if (cannotExecute(target)) return;
        if (!testRequirements(target)) return;

        Runnable runnable = () -> {
            getAction().execute(target, Objects.isNull(context) ? this : context);

            getStorageProvider().store(this, target, LAST_EXECUTION, System.currentTimeMillis());

            getActions().stream()
                    .filter(actionContext -> actionContext.isTargetType(target.getSource()))
                    .map(actionContext -> (ActionContext<TTarget, ?>) actionContext)
                    .forEach(actionContext -> actionContext.execute(target));
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
     * @return true if action was already executed and should only execute once
     */
    public boolean wasExecutedOnce(Target<TTarget> target) {

        return getOptions().isExecuteOnce() && getLastExecution(target) > 0;
    }

    /**
     * Checks if the action is on cooldown for the given {@link Target}.
     * Will always return false if no cooldown is defined (set to zero).
     *
     * @param target target to check
     * @return true if action is on cooldown
     */
    public boolean isOnCooldown(Target<TTarget> target) {
        long cooldown = getOptions().getCooldown();
        if (cooldown < 1) return false;

        long lastExecution = getLastExecution(target);

        if (lastExecution < 1) return false;

        return System.currentTimeMillis() < lastExecution + cooldown;
    }

    private boolean cannotExecute(Target<TTarget> target) {
        return wasExecutedOnce(target) || isOnCooldown(target);
    }

    private long getLastExecution(Target<TTarget> target) {
        return getStorageProvider().get(this, target, LAST_EXECUTION, Long.class).orElse(0L);
    }
}
