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

import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.AbstractScope;

import java.util.Optional;

public abstract class AbstractArtObjectContext extends AbstractScope implements ArtObjectContext {

    @Getter
    private final Class<?> targetClass;

    public AbstractArtObjectContext(@NonNull Configuration configuration, @NonNull Class<?> targetClass) {
        super(configuration);
        this.targetClass = targetClass;
    }

    public boolean isTargetType(Object target) {
        if (target instanceof Target) {
            return getTargetClass().isInstance(((Target<?>) target).getSource());
        }
        return getTargetClass().isInstance(target);
    }

    @Override
    public <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value) {
        return configuration().storage().set(getStorageKey(target, key), value);
    }

    @Override
    public <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass) {
        return configuration().storage().get(getStorageKey(target, key), valueClass);
    }

    private String getStorageKey(Target<?> target, String key) {
        return getUniqueId() + "#" + target.getUniqueId() + "#" + key;
    }
}
