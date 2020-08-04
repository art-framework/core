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
import io.artframework.conf.ActionConfig;
import io.artframework.conf.Constants;
import io.artframework.events.ActionExecutedEvent;
import io.artframework.events.ActionExecutionEvent;
import io.artframework.events.PreActionExecutionEvent;
import io.artframework.util.TimeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * The action context is created for every unique {@link Action} configuration.
 * It holds all relevant information to execute the action and tracks the dependencies.
 *
 * @param <TTarget> target type of the action
 */
@Accessors(fluent = true)
public final class DefaultActionContext<TTarget> extends AbstractArtObjectContext<Action<TTarget>> implements ActionContext<TTarget>, FutureTargetResultCreator {

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
            @NonNull ArtObjectMeta<Action<TTarget>> information,
            @NonNull Action<TTarget> action,
            @NonNull ActionConfig config
    ) {
        super(configuration, information);
        this.action = action;
        this.config = config;
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

        if (ART.callEvent(new PreActionExecutionEvent<>(action(), context)).isCancelled()) {
            return cancelled(target, this);
        }

        if (!isTargetType(target)) return empty(target, this);
        FutureResult executionTest = testExecution(target);
        if (executionTest.failure()) return executionTest;
        CombinedResult requirementTest = testRequirements(context);
        if (requirementTest.failure()) return of(requirementTest, target, this);

        final FutureResult result = empty(target, this);

        Runnable runnable = () -> {

            if (ART.callEvent(new ActionExecutionEvent<>(action(), context)).isCancelled()) {
                result.complete(cancelled(target, this));
                return;
            }

            Result actionResult = action().execute(target, context).with(target, this);

            ART.callEvent(new ActionExecutedEvent<>(action(), context));

            store(target, Constants.Storage.LAST_EXECUTION, System.currentTimeMillis());

            FutureResult nestedActionResult = this.actions().stream()
                    .filter(actionContext -> actionContext.isTargetType(target))
                    .map(actionContext -> (ActionContext<TTarget>) actionContext)
                    .map(action -> action.execute(target, context.next(action)))
                    .reduce(FutureResult::combine)
                    .orElse(result);

            result.complete(actionResult.combine(nestedActionResult));
        };

        long delay = this.config().delay();

        if (configuration().scheduler().isPresent() && delay > 0) {
            configuration().scheduler().get().runTaskLater(runnable, delay);
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

        if (!this.config().executeOnce()) return empty(target, this);

        if (getLastExecution(target) > 0) {
            return failure(target, this, "Action can only be executed once and was already executed.");
        } else {
            return success(target, this);
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
        long cooldown = this.config().cooldown();
        if (cooldown < 1) return empty(target, this);

        long lastExecution = getLastExecution(target);

        if (lastExecution < 1) return success(target, this);

        long remainingCooldown = (lastExecution + cooldown) - System.currentTimeMillis();

        if (remainingCooldown > 0) {
            return failure(target, this, "Action is still on cooldown. "
                    + TimeUtil.getAccurrateShortFormatedTime(remainingCooldown) + " are remaining.");
        } else {
            return success(target, this);
        }
    }

    private FutureResult testExecution(Target<TTarget> target) {
        return testExecutedOnce(target).combine(testCooldown(target));
    }

    private long getLastExecution(Target<TTarget> target) {
        return store(target, Constants.Storage.LAST_EXECUTION, Long.class).orElse(0L);
    }
}
