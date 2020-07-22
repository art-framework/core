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

import java.util.Collection;

public interface RequirementHolder {

    void addRequirement(RequirementContext<?> requirement);

    Collection<RequirementContext<?>> getRequirements();

    @SuppressWarnings("unchecked")
    default <TTarget> TestResult testRequirements(ExecutionContext<?> executionContext) {
        TestResult testResult = TestResult.success();

        for (Target<?> target : executionContext.getTargets()) {
            testResult = testResult.combine(getRequirements().stream()
                    .filter(requirementContext -> requirementContext.isTargetType(target))
                    .map(requirementContext -> (RequirementContext<TTarget>) requirementContext)
                    .map(executionContext::next)
                    .map(context -> context.current().test((Target<TTarget>) target, context))
                    .map(result -> result.combine(target))
                    .reduce(TestResult::combine)
                    .orElse(testResult));
        }

        return testResult;
    }
}
