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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface Trigger extends ArtObject {

    default CombinedResult trigger(String identifier, TriggerTarget<?>... targets) {
        return ART.trigger(identifier, targets);
    }

    default CombinedResult trigger(String identifier, Target<?>... targets) {
        return ART.trigger(identifier, targets);
    }

    default CombinedResult trigger(String identifier, Object... targets) {
        return trigger(identifier, Arrays.stream(targets)
                .map(Target::of)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(Target[]::new));
    }

    default <TTarget> TriggerTarget<TTarget> of(Target<TTarget> target) {
        return new TriggerTarget<>(target);
    }

    @Nullable
    default <TTarget> TriggerTarget<TTarget> of(TTarget target) {
        return Target.of(target)
                .map(TriggerTarget::new)
                .orElse(null);
    }

    default <TTarget> TriggerTarget<TTarget> of(Target<TTarget> target, BiPredicate<Target<TTarget>, ExecutionContext<TriggerContext>> predicate) {
        return new TriggerTarget<>(target, predicate);
    }

    @Nullable
    default <TTarget> TriggerTarget<TTarget> of(TTarget target, BiPredicate<Target<TTarget>, ExecutionContext<TriggerContext>> predicate) {
        return Target.of(target)
                .map(targetTarget -> new TriggerTarget<>(targetTarget, predicate))
                .orElse(null);
    }

}
