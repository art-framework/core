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

import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public interface Context extends Scope {

    /**
     * Get all custom data from this <code>Scope</code>.
     * <p>All data set like this is non persistent and only exists for the lifetime of this {@link Scope}.
     * Use the {@link Storage} to store persistent data.</p>
     * <p>This is custom data that was previously set to the context using
     * {@link #data(String, Object)}. Use custom data if you want to pass data
     * to {@link Context} objects for a given {@link Scope}.</p>
     *
     * @return The custom data. This is never <code>null</code>
     */
    @NonNull
    Map<String, Object> data();

    /**
     * Set some custom data to this <code>Scope</code>.
     * <p>All data set like this is non persistent and only exists for the lifetime of this {@link Scope}.
     * Use the {@link Storage} to store persistent data.</p>
     *
     * @param key A key to identify the custom data
     * @param value The custom data. A null value will remove the entry from the data store.
     * @param <TValue> The type of the value
     * @return The previously set custom data or <code>Optional.empty()</code> if no data
     *         was previously set for the given key or if the current data cannot be cast
     *         to the new data type.
     */
    @SuppressWarnings("unchecked")
    default <TValue> Optional<TValue> data(@NonNull String key, @Nullable TValue value) {
        Object existingData;
        if (value == null) {
            existingData = data().remove(key);
        } else {
            existingData = data().put(key, value);
        }

        if (existingData != null && value != null) {
            try {
                return Optional.of((TValue) value.getClass().cast(existingData));
            } catch (ClassCastException ignored) {
            }
        }

        return Optional.empty();
    }

    /**
     * Get some custom data from this <code>Scope</code>.
     * <p>All data set like this is non persistent and only exists for the lifetime of this {@link Scope}.
     * Use the {@link Storage} to store persistent data.</p>
     * <p>This is custom data that was previously set to the context using
     * {@link #data(String, Object)}. Use custom data if you want to pass data
     * to {@link Context} objects for a given {@link Scope}</p>
     *
     * @param key A key to identify the custom data
     * @param valueClass The type class of the requested value
     * @param <TValue> The type of the value
     * @return The custom data or <code>Optional.empty()</code> if no such data is contained
     *         in this <code>Scope</code>
     */
    default <TValue> Optional<TValue> data(String key, Class<TValue> valueClass) {
        if (!data().containsKey(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(valueClass.cast(data().get(key)));
    }

    default <TValue> Optional<TValue> data(Target<?> target, String key, TValue value) {
        return data(target.getUniqueId() + "#" + key, value);
    }

    default <TValue> Optional<TValue> data(Target<?> target, String key, Class<TValue> valueClass) {
        return data(target.getUniqueId() + "#" + key, valueClass);
    }
}
