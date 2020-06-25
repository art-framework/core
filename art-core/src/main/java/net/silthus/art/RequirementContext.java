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

import lombok.Getter;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.requirements.RequirementConfig;

import java.util.Objects;

/**
 * The requirement context is created for every unique {@link Requirement} configuration.
 * It holds all relevant information to check the requirement and tracks the dependencies.
 *
 * @param <TTarget> target type of the requirement
 * @param <TConfig> config type of the requirement
 */
public class RequirementContext<TTarget, TConfig> extends ArtContext<TTarget, TConfig, RequirementConfig<TConfig>> implements Requirement<TTarget, TConfig> {

    @Getter
    private final Requirement<TTarget, TConfig> requirement;

    public RequirementContext(Class<TTarget> targetClass, Requirement<TTarget, TConfig> requirement, RequirementConfig<TConfig> config) {
        super(targetClass, config);
        Objects.requireNonNull(requirement, "requirement must not be null");
        this.requirement = requirement;
    }

    final boolean test(TTarget target) {

        return test(target, this);
    }

    @Override
    public boolean test(TTarget target, RequirementContext<TTarget, TConfig> context) {

        if (context != null && context != this)
            throw new UnsupportedOperationException("RequirementContext#test(target, context) must not be called directly. Use ActionResult#test(target) instead.");

        Objects.requireNonNull(target, "target must not be null");

        if (!isTargetType(target)) return true;

        return getRequirement().test(target, Objects.isNull(context) ? this : context);
    }
}
