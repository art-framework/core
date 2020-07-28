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

import io.artframework.impl.DefaultMapStorage;
import lombok.NonNull;

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
public interface Storage extends Scope {

    static Storage of(Configuration configuration) {
        return new DefaultMapStorage(configuration);
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
     *
     * Will override any existing value that has the same key
     * and return that value.
     *
     * Will return an empty {@link Optional} if this is the first entry for the given key.
     *
     * @param target   target to store value for
     * @param key      storage key
     * @param value    value to store
     * @param <TValue> type of the value
     * @return an {@link Optional} containing the existing value
     * @see #set(String, Object)
     */
    default <TValue> Optional<TValue> set(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value) {
        return set(target.uniqueId() + "#" + key, value);
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
    <TValue> Optional<TValue> get(String key, Class<TValue> valueClass);

    /**
     * Retrieves a value stored for the given target.
     * Will return the default value of the type or an empty {@link Optional} if the storage
     * key is not found or the stored value cannot be cast to the needed type.
     *
     * @param <TValue>   type of the value
     * @param target     target to retrieve value for
     * @param key        storage key
     * @param valueClass class of the value
     * @return stored value or empty result if the value does not exist or cannot be cast into the value type.
     */
    default <TValue> Optional<TValue> get(Target<?> target, String key, Class<TValue> valueClass) {
        return get(target.uniqueId() + "#" + key, valueClass);
    }
}
