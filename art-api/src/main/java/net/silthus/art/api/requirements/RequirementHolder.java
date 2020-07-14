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

package net.silthus.art.api.requirements;

import net.silthus.art.ExecutionContext;
import net.silthus.art.impl.DefaultRequirementContext;

import java.util.Collection;

public interface RequirementHolder {

    void addRequirement(DefaultRequirementContext<?> requirement);

    Collection<DefaultRequirementContext<?>> getRequirements();

    @SuppressWarnings("unchecked")
    default <TTarget> boolean testRequirements(ExecutionContext<TTarget, ?> context) {
        return getRequirements().stream()
                .filter(requirementContext -> requirementContext.isTargetType(context.target()))
                .map(requirementContext -> (DefaultRequirementContext<TTarget>) requirementContext)
                .allMatch(requirementContext -> requirementContext.test(context.next(requirementContext)));
    }
}
