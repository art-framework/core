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

package net.silthus.art.api;

import net.silthus.art.ART;
import net.silthus.art.api.target.Target;
import net.silthus.art.api.trigger.TriggerContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public interface Trigger extends ArtObject {

    default <TConfig> void trigger(String identifier, Predicate<TriggerContext<TConfig>> context, Target<?>... targets) {
        ART.trigger(identifier, context, targets);
    }

    default <TConfig> void trigger(String identifier, Predicate<TriggerContext<TConfig>> context, Object... targets) {
        ART.trigger(identifier, context, Arrays.stream(targets)
                .map(Target::of)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(Target[]::new));
    }
}
