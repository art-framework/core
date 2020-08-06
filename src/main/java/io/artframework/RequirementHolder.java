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

package io.artframework;

import java.util.Collection;

public interface RequirementHolder {

    void addRequirement(RequirementContext<?> requirement);

    Collection<RequirementContext<?>> requirements();

    default CombinedResult testRequirements(ExecutionContext<?> context) {
        return context.targets().stream()
                .map(target -> testRequirements(target, context))
                .reduce(Result::combine)
                .orElse(CombinedResult.empty());
    }

    @SuppressWarnings("unchecked")
    default <TTarget> CombinedResult testRequirements(Target<TTarget> target, ExecutionContext<?> context) {
        return requirements().stream()
                .filter(requirementContext -> requirementContext.isTargetType(target))
                .map(requirementContext -> (RequirementContext<TTarget>) requirementContext)
                .map(requirementContext -> requirementContext.test(target, context.next(requirementContext)))
                .map(Result::combine)
                .reduce(Result::combine)
                .orElse(CombinedResult.empty());
    }
}
