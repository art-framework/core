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
import net.silthus.art.api.Action;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.requirements.RequirementHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public ActionContext(Class<TTarget> tTargetClass, Action<TTarget, TConfig> action, ActionConfig<TConfig> config) {
        super(tTargetClass, config);
        Objects.requireNonNull(action);
        this.action = action;
    }

    @Override
    public void addAction(ActionContext<?, ?> action) {
        this.actions.add(action);
    }

    @Override
    public final void addRequirement(RequirementContext<?, ?> requirement) {
        this.requirements.add(requirement);
    }

    public final void execute(TTarget target) {

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

        getActions().stream()
                .filter(actionContext -> actionContext.isTargetType(target))
                .map(actionContext -> (ActionContext<TTarget, ?>) actionContext)
                .forEach(actionContext -> actionContext.execute(target));
    }
}
