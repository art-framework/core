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

/**
 * Provides the option to provide a predicate with a config that
 * is checked before the trigger is executed.
 *
 * @param <TTarget> the type of the target
 * @param <TConfig> the type of the config
 */
@FunctionalInterface
public interface TriggerRequirement<TTarget, TConfig> extends ResultCreator {

    /**
     * Tests the given trigger and target against the config and returns the result of the check.
     * <p>
     * Use the {@link #success(String...)} and {@link #failure(String...)} methods to provide a result.
     *
     * @param target the target to check
     * @param context the context of the check
     * @param config the config of the check
     * @return the result of the check
     */
    Result test(Target<TTarget> target, ExecutionContext<TriggerContext> context, TConfig config);
}
