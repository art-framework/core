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
public class ActionContext<TTarget, TConfig> extends ArtContext<TTarget, TConfig, ActionConfig<TConfig>> implements Action<TTarget, TConfig> {

    @Getter(AccessLevel.PROTECTED)
    private final Action<TTarget, TConfig> action;
    @Getter(AccessLevel.PROTECTED)
    private final List<ActionContext<?, ?>> nestedActions = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED)
    private final List<RequirementContext<?, ?>> requirements = new ArrayList<>();

    public ActionContext(Class<TTarget> tTargetClass, Action<TTarget, TConfig> action, ActionConfig<TConfig> config) {
        super(tTargetClass, config);
        this.action = action;
    }

    public void addNestedAction(ActionContext<?, ?> action) {
        this.nestedActions.add(action);
    }

    public void addRequirements(Collection<RequirementContext<?, ?>> requirements) {
        this.requirements.addAll(requirements);
    }

    final void execute(TTarget target) {

        getAction().execute(target, this);
    }

    @Override
    public void execute(TTarget target, ActionContext<TTarget, TConfig> context) {

        if (!isTargetType(target)) return;

        getAction().execute(target, Objects.isNull(context) ? this : context);
    }
}
