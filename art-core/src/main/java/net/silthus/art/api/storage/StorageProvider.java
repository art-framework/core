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

package net.silthus.art.api.storage;

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtObject;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.target.Target;
import net.silthus.art.storage.MemoryStorageProvider;

import java.util.Optional;

/**
 * The {@link StorageProvider} provides a way to store metadata for a given {@link Target}
 * and {@link ArtObject}.
 * The {@link StorageProvider} can be replaced with the required implementation.
 * If no implementation is provided it will fall back to the {@link MemoryStorageProvider}.
 */
public interface StorageProvider {

    /**
     * Stores a global value for the given {@link Target}.
     * The value can be accessed and overwritten by anyone.
     * <br>
     * Use the {@link #store(ArtContext, Target, String, Object)} method
     * to link your storage to the given {@link ArtObject} instance.
     *
     * @param target   target to store value for
     * @param key      storage key
     * @param value    value to store
     * @param <TValue> type of the value
     * @see #store(ArtContext, Target, String, Object)
     */
    <TValue> void store(Target<?> target, String key, TValue value);

    /**
     * Stores a value for the given {@link Target} that is unique to
     * the provided {@link ArtContext}.
     * <br>
     * The identifier of the context is computed from a hashcode
     * of the configured ART string and line number paired with the
     * unique identifier of the {@link Target} and {@link ArtConfig}.
     * <br>
     * Use the {@link #store(Target, String, Object)} method to store data globally
     * detached from the {@link ArtContext}.
     *
     * @param context  context to store value for
     * @param target   target to store value for
     * @param key      storage key
     * @param value    value to store
     * @param <TValue> type of the value
     * @see #store(Target, String, Object)
     */
    <TValue> void store(ArtContext<?, ?, ?> context, Target<?> target, String key, TValue value);

    /**
     * Retrieves a globally stored value from the store.
     * Will return the default value of the type or an empty {@link Optional} if the storage
     * key is not found or the stored value cannot be cast to the needed type.
     *
     * @param <TValue>   type of the value
     * @param target     target to retrieve value for
     * @param key        storage key
     * @param valueClass class of the value
     * @return stored value or empty result if the value does not exist or cannot be cast into the value type.
     */
    <TValue> Optional<TValue> get(Target<?> target, String key, Class<TValue> valueClass);

    /**
     * Retrieves a {@link ArtContext} related value from the store.
     * Will return the default value of the type or an empty {@link Optional} if the storage
     * key is not found or the stored value cannot be cast to the needed type.
     *
     * @param <TValue>   type of the value
     * @param context    context to retrieve value for
     * @param target     target to retrieve value for
     * @param key        storage key
     * @param valueClass class of the value
     * @return stored value or empty result if the value does not exist or cannot be cast into the value type.
     */
    <TValue> Optional<TValue> get(ArtContext<?, ?, ?> context, Target<?> target, String key, Class<TValue> valueClass);
}
