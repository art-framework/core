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

package net.silthus.art;

import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The action context is created for every unique {@link Action} configuration.
 * It holds all relevant information to execute the action and tracks the dependencies.
 *
 * @param <TTarget> target type of the action
 * @param <TConfig> config type of the action
 */
public final class ActionContext<TTarget, TConfig> extends ArtContext<TTarget, TConfig, ActionConfig<TConfig>> implements Action<TTarget, TConfig> {

    @Getter(AccessLevel.PROTECTED)
    private final Action<TTarget, TConfig> action;
    @Getter(AccessLevel.PROTECTED)
    private final List<ActionContext<?, ?>> childActions = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED)
    private final List<RequirementContext<?, ?>> requirements = new ArrayList<>();

    public ActionContext(Class<TTarget> tTargetClass, Action<TTarget, TConfig> action, ActionConfig<TConfig> config) {
        super(tTargetClass, config);
        Objects.requireNonNull(action);
        this.action = action;
    }

    final void addChildAction(ActionContext<?, ?> action) {
        this.childActions.add(action);
    }

    final void addRequirements(Collection<RequirementContext<?, ?>> requirements) {
        this.requirements.addAll(requirements);
    }

    final void execute(TTarget target) {

        execute(target, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void execute(TTarget target, ActionContext<TTarget, TConfig> context) {

        if (context != null && context != this)
            throw new UnsupportedOperationException("ActionContext#execute(target, context) must not be called directly. Use ActionResult#execute(target) instead.");

        Objects.requireNonNull(target, "target must not be null");

        if (!isTargetType(target)) return;
        if (!testRequirements(target)) return;

        getAction().execute(target, Objects.isNull(context) ? this : context);

        getChildActions().stream()
                .filter(actionContext -> actionContext.isTargetType(target))
                .map(actionContext -> (ActionContext<TTarget, ?>) actionContext)
                .forEach(actionContext -> actionContext.execute(target));
    }

    @SuppressWarnings("unchecked")
    private boolean testRequirements(TTarget target) {
        return getRequirements().stream()
                .filter(requirement -> requirement.isTargetType(target))
                .map(requirement -> (RequirementContext<TTarget, ?>) requirement)
                .allMatch(requirement -> requirement.test(target));
    }
}
