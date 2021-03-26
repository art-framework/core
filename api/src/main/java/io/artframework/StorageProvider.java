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

import io.artframework.impl.DefaultMapStorageProvider;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Use the storage to set and get data for a given {@link Target} and/or {@link Context}.
 * Use cases for this could be storing a counter or a cooldown timer.
 * Or you could use this to store some global properties that need to be shared.
 *
 * You can store any object that can be serialized into a JSON object.
 * You should try to keep the objects you store simple and primitive values at best.
 *
 * There are also convenience methods on the {@link Context} to set and get data.
 * These methods just delegate to the implementing storage provider.
 * <p>
 *     <pre>
 *         // set a global property foo to true
 *         context.set("foo", true);
 *         // get the value somewhere later on
 *         Optional<Boolean> myValue = context.get("foo", Boolean.class);
 *     </pre>
 * </p>
 */
public interface StorageProvider extends Scoped, AutoCloseable {

    static StorageProvider of(Scope scope) {
        return new DefaultMapStorageProvider(scope);
    }

    /**
     * Stores value for the module into the persistent metadata store and returns the value that was previously stored.
     * If no value was stored previously and empty optional will be returned.
     *
     * @param module the module that is used to store the value. can be null.
     * @param key the key to store the value under
     * @param value the value to store
     * @param <TValue> the type of the value
     * @return the previously stored value or an empty optional
     */
    default <TValue> Optional<TValue> set(@Nullable Module module, @NonNull String key, @NonNull TValue value) {
        return Optional.ofNullable(module)
                .map(Module::metadata)
                .map(info -> "module#" + info.identifier() + "#" + key)
                .flatMap(storageKey -> set(storageKey, value));
    }

    /**
     * Stores a value in the persistent metadata store and returns any value
     * that was already stored. If no value was found an empty {@link Optional} is returned.
     *
     * @param key The unique key to store a value for.
     * @param value The value to store.
     * @param <TValue> type of the value
     * @return existing value if it exists and is of the same type otherwise an empty {@link Optional}
     */
    <TValue> Optional<TValue> set(@NonNull String key, @NonNull TValue value);

    /**
     * Stores a value for the given {@link Target}.
     * <p>Will override any existing value that has the same key
     * and return that value.
     * <p>Will return an empty {@link Optional} if this is the first entry for the given key.
     *
     * @param target   target to store value for
     * @param key      storage key
     * @param value    value to store
     * @param <TValue> type of the value
     * @return an {@link Optional} containing the existing value
     * @see #set(String, Object)
     */
    default <TValue> Optional<TValue> set(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value) {
        return set("target#" + target.uniqueId() + "#" + key, value);
    }

    /**
     * Stores a value for the given context and target under the given key.
     * <p>The value will be scoped to the storage key and unique id of the context and target.
     * <p>Returns any existing value that was replaced or an empty optional if no value existed.
     *
     * @param context the context that stores the data
     * @param target the target to store the data for
     * @param key the key to store the data under
     * @param value the value to store
     * @param <TValue> the type of the value
     * @return the previously stored value if it existed and has the same type as the new value
     */
    default <TValue> Optional<TValue> set(@NonNull ArtObjectContext<?> context, @NonNull Target<?> target, @NonNull String key, TValue value) {

        return set(context.uniqueId() + "#" + context.storageKey() + "#" + target.uniqueId() + "#" + key, value);
    }

    /**
     * Gets a value stored for the given module under the given key.
     *
     * @param module the module to get data for
     * @param key the key of the storage
     * @param valueClass the class of the data
     * @param <TValue> the type of the data
     * @return the stored value or an empty optional
     */
    default <TValue> Optional<TValue> get(@Nullable Module module, @NonNull String key, @NonNull Class<TValue> valueClass) {
        return Optional.ofNullable(module)
                .map(Module::metadata)
                .map(info -> "module#" + info.identifier() + "#" + key)
                .flatMap(storageKey -> get(storageKey, valueClass));
    }

    /**
     * Retrieves a stored setting from the store.
     * Will return the default value of the type or an empty {@link Optional} if the storage
     * key is not found or the stored value cannot be cast to the needed type.
     *
     * @param key storage key
     * @param valueClass class of the value
     * @param <TValue> type of the value
     * @return stored value or empty result if the value does not exist or cannot be cast into the value type.
     */
    <TValue> Optional<TValue> get(@NonNull String key, @NonNull Class<TValue> valueClass);

    /**
     * Retrieves a value stored for the given target.
     * Will return the default value of the type or an empty {@link Optional} if the storage
     * key is not found or the stored value cannot be cast to the needed type.
     *
     * @param <TValue>   type of the value
     * @param target     target to retrieve value for
     * @param key        storage key
     * @param valueClass class of the value
     * @return stored value or empty result if the value does not exist or cannot be cast into the value type
     */
    default <TValue> Optional<TValue> get(@NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass) {
        return get("target#" + target.uniqueId() + "#" + key, valueClass);
    }

    /**
     * Retrieves a value stored for the given context and target.
     * <p>The storage provider will use the targets storage key and unique id combined
     * with the target id and the provided key to get the unique entry.
     *
     * @param context the context that stores the information
     * @param target the target to retrieve a value for
     * @param key the key the value is stored under
     * @param valueClass the class of the value
     * @param <TValue> the type of the value
     * @return stored value or empty result if the value does not exist or cannot be cast into the value type
     */
    default <TValue> Optional<TValue> get(@NonNull ArtObjectContext<?> context, @NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass) {
        return get(context.uniqueId() + "#" + context.storageKey() + "#" + target.uniqueId() + "#" + key, valueClass);
    }
}
