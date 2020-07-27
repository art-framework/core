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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public interface CombinedResult extends Result {

    static CombinedResult empty(String... messages) {
        return of(Result.empty(messages));
    }

    static CombinedResult of(Result... results) {
        return new DefaultCombinedResult(Arrays.asList(results));
    }

    Collection<Result> results();

    default <TTarget> Collection<TargetResult<TTarget, ?, ?>> ofTarget(TTarget target) {
        return Target.of(target).map(this::ofTarget).orElse(new ArrayList<>());
    }

    <TTarget> Collection<TargetResult<TTarget, ?, ?>> ofTarget(Target<TTarget> target);

    <TTarget> Collection<TargetResult<TTarget, ?, ?>> ofTarget(Class<TTarget> targetClass);
}
