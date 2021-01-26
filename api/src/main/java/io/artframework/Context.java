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
import java.util.function.Supplier;

public interface Context extends Scoped {

    /**
     * Get all custom data from this <code>Scope</code>.
     * <p>All data set like this is non persistent and only exists for the lifetime of this {@link Scoped}.
     * Use the {@link StorageProvider} to store persistent data.</p>
     * <p>This is custom data that was previously set to the context using
     * {@link #data(String, Object)}. Use custom data if you want to pass data
     * to {@link Context} objects for a given {@link Scoped}.</p>
     *
     * @return The custom data. This is never <code>null</code>
     */
    @NonNull
    Map<String, Object> data();

    /**
     * Set some custom data to this <code>Scope</code>.
     * <p>All data set like this is non persistent and only exists for the lifetime of this {@link Scoped}.
     * Use the {@link StorageProvider} to store persistent data.</p>
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
     * <p>All data set like this is non persistent and only exists for the lifetime of this {@link Scoped}.
     * Use the {@link StorageProvider} to store persistent data.</p>
     * <p>This is custom data that was previously set to the context using
     * {@link #data(String, Object)}. Use custom data if you want to pass data
     * to {@link Context} objects for a given {@link Scoped}</p>
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
        return data(target.uniqueId() + "#" + key, value);
    }

    default <TValue> Optional<TValue> data(Target<?> target, String key, Class<TValue> valueClass) {
        return data(target.uniqueId() + "#" + key, valueClass);
    }

    /**
     * Use the variables map to directly modify the variables stored in this context.
     * <p>Any modification to it will be transparent to the root variable store.
     *
     * @return a reference to the variable store
     */
    Map<String, Variable<?>> variables();

    /**
     * Tries to find a variable with the given key.
     *
     * @param key the key of the variable
     * @return the value of the variable
     */
    default Optional<Object> var(@NonNull String key) {

        return Optional.ofNullable(variables().get(key))
                .map(Variable::value);
    }

    /**
     * Tries to find a variable with the given key and casts it to the provided type.
     * <p>An empty optional is returned if the value of the variable does not match the given type
     * or if no variable with the given key is found.
     *
     * @param key the key of the variable
     * @param type the class of the variable type
     * @param <TValue> the type of the variable
     * @return the value of the variable cast to the given type
     */
    default <TValue> Optional<TValue> var(@NonNull String key, @NonNull Class<TValue> type) {

        return Optional.ofNullable(variables().get(key))
                .filter(variable -> type.isAssignableFrom(variable.type()))
                .map(variable -> type.cast(variable.value()));
    }

    /**
     * Stores a new variable under the given key.
     * <p>Any existing variable with the same key is overwritten.
     * Use the {@link #varIfAbsent(String, Object)} method to honor existing variables.
     *
     * @param key      the key of the variable
     * @param value    the value of the variable
     * @param <TValue> the type of the variable
     * @return this execution context
     */
    default <TValue> Context var(@NonNull String key, @NonNull TValue value) {

        variables().put(key, new Variable<>(key, value));

        return this;
    }

    /**
     * Stores a new variable under the given key that is resolved using the
     * provided supplier once requested.
     * <p>Any existing variable with the same key is overwritten.
     * Use the {@link #varIfAbsent(String, Class, Supplier)} method to honor existing variables.
     *
     * @param key the key of the variable
     * @param type the class of the variable type
     * @param value the function that is called when the variable value is requested
     * @param <TValue> the type of the variable
     * @return this execution context
     */
    default <TValue> Context var(@NonNull String key, Class<TValue> type, @NonNull Supplier<TValue> value) {

        variables().put(key, new Variable<>(key, type, value));

        return this;
    }

    /**
     * Stores a new variable under the given key if no variable with the same key exists.
     * <p>Use the {@link #var(String, Object)} method to overwrite any existing variable.
     *
     * @param key the key of the variable
     * @param value the value of the variable
     * @param <TValue> the type of the variable
     * @return this execution context
     */
    default <TValue> Context varIfAbsent(@NonNull String key, @NonNull TValue value) {

        variables().putIfAbsent(key, new Variable<>(key, value));

        return this;
    }

    /**
     * Stores a new variable under the given key that is resolved using the
     * provided supplier once requested.
     * <p>Use the {@link #var(String, Class, Supplier)} method to overwrite any existing variable.
     *
     * @param key the key of the variable
     * @param type the class of the variable type
     * @param value the function that is called when the variable value is requested
     * @param <TValue> the type of the variable
     * @return this execution context
     */
    default <TValue> Context varIfAbsent(@NonNull String key, Class<TValue> type, @NonNull Supplier<TValue> value) {

        variables().putIfAbsent(key, new Variable<>(key, type, value));

        return this;
    }
}
