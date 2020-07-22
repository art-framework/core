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

import lombok.NonNull;

/**
 * A {@link Requirement} can be used to filter {@link Action}s, {@link Trigger}
 * or be used inside other plugins for filtering.
 * <br>
 * Register your requirements by calling {@link ART#register()}.
 * This will wrap the {@link Requirement} into a {@link RequirementContext} and bundle it with the configured options.
 * <br>
 * A requirement will only be used for filtering if the target type matches with the object that is being filtered.
 * <br>
 * For example: an {@link Action} that is targeting players can only be filtered by requirements
 * that also target players.
 *
 * @param <TTarget> target type this requirement applies too. Defines the filtering scope.
 * @see Context
 * @see Action
 * @see Trigger
 */
@FunctionalInterface
public interface Requirement<TTarget> extends ArtObject {

    /**
     * Tests if this requirement should filter {@link Action}s or {@link Trigger}s for the given target.
     * Return true for the check to pass and not to filter out the action.
     * Return false if the check fails and the actions should be filtered.
     *
     * @param target the target that is checked in this requirement
     * @param context the context that holds the config and context of the check
     * @return false if the check fails and the filter should be applied or true if all checks pass and no filtering should be applied.
     */
    TestResult test(@NonNull Target<TTarget> target, @NonNull ExecutionContext<RequirementContext<TTarget>> context);

    default TestResult resultOf(boolean result, String... reasons) {
        return result ? success() : failure(reasons);
    }

    default TestResult success() {
        return TestResult.success();
    }

    default TestResult failure(String... reasons) {
        return TestResult.failure();
    }

    default TestResult error(ErrorCode errorCode, String... messages) {
        return TestResult.error(errorCode, messages);
    }
}
