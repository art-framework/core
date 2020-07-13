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

import java.util.Optional;

public interface Context extends Scope {

    default <TTarget> Optional<Target<TTarget>> target(TTarget source) {
        return configuration().targets().get(source);
    }

    default <TValue> Optional<TValue> data(Target<?> target, String key, TValue value) {
        return data(target.getUniqueId() + "#" + key, value);
    }

    default <TValue> Optional<TValue> data(Target<?> target, String key, Class<TValue> valueClass) {
        return data(target.getUniqueId() + "#" + key, valueClass);
    }
}
