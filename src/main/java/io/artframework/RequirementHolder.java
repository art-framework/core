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

    @SuppressWarnings("unchecked")
    default <TTarget> CombinedResult testRequirements(ExecutionContext<?> executionContext) {
        CombinedResult result = CombinedResult.empty();

        for (Target<?> target : executionContext.getTargets()) {
            result = result.combine(requirements().stream()
                    .filter(requirementContext -> requirementContext.isTargetType(target))
                    .map(requirementContext -> (RequirementContext<TTarget>) requirementContext)
                    .map(executionContext::next)
                    .map(context -> context.current().test((Target<TTarget>) target, context))
                    .map(CombinedResult::of)
                    .reduce(CombinedResult::combine)
                    .orElse(CombinedResult.of(Result.empty())));
        }

        return result;
    }
}
