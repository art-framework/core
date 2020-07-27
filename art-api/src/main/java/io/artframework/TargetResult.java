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

/**
 * The ArtResult holds several other results and combines them into a single result.
 */
public interface TargetResult<TTarget, TArtObject extends ArtObject, TContext extends ArtObjectContext<TArtObject>> extends Result {

    static <TTarget, TArtObject extends ArtObject, TContext extends ArtObjectContext<TArtObject>> TargetResult<TTarget, TArtObject, TContext>
    of(Result result, Target<TTarget> target, TContext context) {
        return new DefaultTargetResult<>(result.status(), result.messages(), target, context);
    }

    Target<TTarget> target();

    default Options<TArtObject> options() {
        return context().options();
    }

    TContext context();
}
