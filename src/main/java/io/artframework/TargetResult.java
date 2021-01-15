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

import io.artframework.impl.DefaultTargetResult;
import lombok.NonNull;

/**
 * The target result contains additional target and context information about a result.
 * <p>
 * One target result will be created for each attempt on a target with a given context.
 * <p>
 * The target result is immutable and calling combine or future on it will create a new clone.
 */
public interface TargetResult<TTarget, TContext extends ArtObjectContext<?>> extends Result {

    /**
     * Creates a new target result from the given result with the given target and context.
     *
     * @param result the result to enhance
     * @param target the target of the result
     * @param context the context that created the result
     * @param <TTarget> the type of the target
     * @param <TContext> the context of the target
     * @return the created target result
     */
    static <TTarget, TContext extends ArtObjectContext<?>> TargetResult<TTarget, TContext> of(
            @NonNull Result result,
            @NonNull Target<TTarget> target,
            @NonNull TContext context
    ) {
        return new DefaultTargetResult<>(result.status(), result.messages(), target, context);
    }

    /**
     * Gets the target associated with this result.
     *
     * @return the target of this result
     */
    Target<TTarget> target();

    /**
     * Gets the context that created this result.
     *
     * @return the context of this result
     */
    TContext context();
}
