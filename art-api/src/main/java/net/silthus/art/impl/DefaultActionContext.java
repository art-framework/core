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

package net.silthus.art.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.conf.ActionConfig;
import net.silthus.art.conf.Constants;
import net.silthus.art.events.ActionExecutedEvent;
import net.silthus.art.events.ActionExecutionEvent;
import net.silthus.art.events.PreActionExecutionEvent;
import net.silthus.art.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * The action context is created for every unique {@link Action} configuration.
 * It holds all relevant information to execute the action and tracks the dependencies.
 *
 * @param <TTarget> target type of the action
 */
public final class DefaultActionContext<TTarget> extends AbstractArtObjectContext<Action<TTarget>> implements ActionContext<TTarget> {

    @Getter(AccessLevel.PROTECTED)
    private final Action<TTarget> action;
    @Getter
    private final ActionConfig config;

    @Getter
    private final List<ActionContext<?>> actions = new ArrayList<>();
    @Getter
    private final List<RequirementContext<?>> requirements = new ArrayList<>();

    public DefaultActionContext(
            @NonNull Configuration configuration,
            @NonNull ArtInformation<Action<TTarget>> information,
            @NonNull Action<TTarget> action,
            @NonNull ActionConfig config
    ) {
        super(configuration, information);
        this.action = action;
        this.config = config;
    }

    @Override
    public @NonNull String getUniqueId() {
        return getConfig().getIdentifier();
    }

    @Override
    public void addAction(ActionContext<?> action) {
        this.actions.add(action);
    }

    @Override
    public final void addRequirement(RequirementContext<?> requirement) {
        this.requirements.add(requirement);
    }

    @Override
    @SuppressWarnings("unchecked")
    public FutureResult execute(Target<TTarget> target, ExecutionContext<ActionContext<TTarget>> context) {

        if (ART.callEvent(new PreActionExecutionEvent<>(getAction(), context)).isCancelled()) {
            return cancelled();
        }

        if (!isTargetType(target)) return empty();
        FutureResult executionTest = testExecution(target);
        if (executionTest.isFailure()) return executionTest;
        CombinedResult requirementTest = testRequirements(context);
        if (requirementTest.isFailure()) return of(requirementTest);

        final FutureResult result = empty();

        Runnable runnable = () -> {

            if (ART.callEvent(new ActionExecutionEvent<>(getAction(), context)).isCancelled()) {
                result.complete(cancelled());
                return;
            }

            getAction().execute(target, context);

            ART.callEvent(new ActionExecutedEvent<>(getAction(), context));

            store(target, Constants.Storage.LAST_EXECUTION, System.currentTimeMillis());

            FutureResult futureResult = getActions().stream()
                    .filter(actionContext -> actionContext.isTargetType(target))
                    .map(actionContext -> (ActionContext<TTarget>) actionContext)
                    .map(action -> action.execute(target, context.next(action)))
                    .reduce(FutureResult::combine)
                    .orElse(result);

            result.complete(futureResult);
        };

        long delay = getConfig().getDelay();

        if (getConfiguration().scheduler().isPresent() && delay > 0) {
            getConfiguration().scheduler().get().runTaskLater(runnable, delay);
        } else {
            runnable.run();
        }

        return result;
    }

    /**
     * Checks if the {@link DefaultActionContext} has the execute_once option
     * and already executed once for the {@link Target}.
     *
     * @param target target to check
     * @return true if action was already executed and should only execute once
     */
    public FutureResult testExecutedOnce(Target<TTarget> target) {

        if (!getConfig().isExecuteOnce()) return empty();

        if (getLastExecution(target) > 0) {
            return failure("Action can only be executed once and was already executed.");
        } else {
            return success();
        }
    }

    /**
     * Checks if the action is on cooldown for the given {@link Target}.
     * Will always return false if no cooldown is defined (set to zero).
     *
     * @param target target to check
     * @return a successful result if the action is not on cooldown a failure otherwise
     */
    public FutureResult testCooldown(Target<TTarget> target) {
        long cooldown = getConfig().getCooldown();
        if (cooldown < 1) return empty();

        long lastExecution = getLastExecution(target);

        if (lastExecution < 1) return success();

        long remainingCooldown = (lastExecution + cooldown) - System.currentTimeMillis();

        if (remainingCooldown > 0) {
            return failure("Action is still on cooldown. "
                    + TimeUtil.getAccurrateShortFormatedTime(remainingCooldown) + " are remaining.");
        } else {
            return success();
        }
    }

    private FutureResult testExecution(Target<TTarget> target) {
        return testExecutedOnce(target).combine(testCooldown(target));
    }

    private long getLastExecution(Target<TTarget> target) {
        return store(target, Constants.Storage.LAST_EXECUTION, Long.class).orElse(0L);
    }
}
