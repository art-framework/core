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

import lombok.Data;

import java.util.function.BiPredicate;

@Data
public final class TriggerTarget<TTarget> {

    private final Target<TTarget> target;
    private final BiPredicate<Target<TTarget>, ExecutionContext<TriggerContext>> predicate;

    TriggerTarget(Target<TTarget> target) {
        this.target = target;
        this.predicate = (t, c) -> true;
    }

    TriggerTarget(Target<TTarget> target, BiPredicate<Target<TTarget>, ExecutionContext<TriggerContext>> predicate) {
        this.target = target;
        this.predicate = predicate;
    }

    public boolean test(ExecutionContext<TriggerContext> executionContext) {
        return predicate.test(target, executionContext);
    }
}
