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
import net.silthus.art.api.storage.StorageConstants;
import net.silthus.art.impl.AbstractScope;

import java.util.Optional;

public abstract class AbstractArtObjectContext extends AbstractScope implements ArtObjectContext {

    @Getter
    private final String uniqueId;
    @Getter
    private final Class<?> targetClass;

    public AbstractArtObjectContext(@NonNull Configuration configuration, @NonNull String uniqueId, @NonNull Class<?> targetClass) {
        super(configuration);
        this.uniqueId = uniqueId;
        this.targetClass = targetClass;
    }

    public boolean isTargetType(Object target) {
        if (target instanceof Target) {
            return getTargetClass().isInstance(((Target<?>) target).getSource());
        }
        return getTargetClass().isInstance(target);
    }

    /**
     * Stores a value for the given {@link Target} and this {@link ArtObjectContext}.
     * This means a unique key is generated from the {@link Target#getUniqueId()} and
     * {@link ArtObjectContext#getUniqueId()} and will be appended by your key.
     * <br>
     * Then the {@link Storage#set(String, Object)} method is called and the data is persisted.
     * <br>
     * Use the {@link #data()} methods to store data that is only available in this scope
     * and not persisted to the database.
     *
     * @param target   target to store value for
     * @param key      storage key
     * @param value    value to store
     * @param <TValue> type of the value
     * @return an {@link Optional} containing the existing value
     * @see Storage#set(String, Object)
     */
    protected <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value) {
        return configuration().storage().set(getStorageKey(target, key), value);
    }

    /**
     * Retrieves a persistently stored value from the {@link Storage} and returns
     * it cast to the given type. Will return an empty {@link Optional} if casting
     * fails or the data does not exist.
     * <br>
     * The data that is fetched will be stored under a unique key combination of
     * {@link ArtObjectContext#getUniqueId()} and {@link Target#getUniqueId()}.
     * <br>
     * Use the {@link #data()} methods to store data that is only available in this scope
     * and not persisted to the database.
     *
     * @param target   target to store value for
     * @param key      storage key
     * @param valueClass class of the value type you expect in return
     * @param <TValue> type of the value
     * @return the stored value or an empty {@link Optional} if the value type cannot be cast or does not exist
     */
    protected <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass) {
        return configuration().storage().get(getStorageKey(target, key), valueClass);
    }

    private String getStorageKey(Target<?> target, String key) {
        return getUniqueId() + "#" + target.getUniqueId() + "#" + key;
    }
}
