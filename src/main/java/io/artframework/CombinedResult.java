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

import io.artframework.impl.DefaultCombinedResult;

import java.util.Arrays;
import java.util.Collection;

/**
 * The CombinedResult combines multiple results and target results and dynamically
 * adjusts its own result based off the results contained within.
 * <p>
 *
 */
public interface CombinedResult extends Result {

    /**
     * Creates a new empty combined result.
     *
     * @param messages optional messages to store in the empty result
     * @return new empty combined result
     */
    static CombinedResult empty(String... messages) {
        return of(Result.empty(messages));
    }

    /**
     * Creates a new combined result from the given results.
     *
     * @param results the results that make up the combined result
     * @return a new combined result with the given results
     */
    static CombinedResult of(Result... results) {
        return new DefaultCombinedResult(Arrays.asList(results));
    }

    /**
     * Returns the list of results in this combined result.
     * <p>
     * The list may contain both types of results: {@link Result} and {@link TargetResult}.
     * You can use the <code>ofTarget(...)</code> methods to get all results for a specific target.
     *
     * @return list of results inside this combined result
     */
    Collection<Result> results();

    /**
     * Gets all results of the specific target.
     * <p>
     * You can create a combined result from the list of results to easily check the success status
     * for all of the target results.
     * <code>
     *     {@link CombinedResult#of(Result...)#success()}
     * </code>
     *
     * @param target the target source to get the results for
     * @param <TTarget> type of the target
     * @return a list of results of the given target. the list may be empty but is never null.
     */
    <TTarget> Collection<TargetResult<TTarget, ?>> ofTarget(Target<TTarget> target);

    /**
     * Gets all results of the specific target type.
     * <p>
     * You can create a combined result from the list of results to easily check the success status
     * for all of the target results.
     * <code>
     *     {@link CombinedResult#of(Result...)#success()}
     * </code>
     *
     * @param targetClass class of the target type
     * @param <TTarget> type of the target
     * @return a list of results of the given target type. the list may be empty but is never null.
     */
    <TTarget> Collection<TargetResult<TTarget, ?>> ofTarget(Class<TTarget> targetClass);
}
