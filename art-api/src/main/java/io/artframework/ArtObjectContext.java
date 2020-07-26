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
import java.util.Optional;

/**
 * Every {@link ArtObject} must be wrapped inside an {@link ArtObjectContext}
 * that controls how the {@link ArtObject} is executed or tested. It also provides
 * a way to access the {@link Configuration} and defines a unique id that will be used
 * to store data for the {@link ArtObject}.
 */
public interface ArtObjectContext<TArtObject extends ArtObject> extends Context, CombinedResultCreator {

    /**
     * Gets the unique id of this {@link ArtObject} context.
     * The unique id is used to store metadata about the context.
     *
     * @return the unique id of this context
     */
    @NonNull
    String getUniqueId();

    /**
     * Gets the target type that is used by the underlying
     * {@link ArtObject} of this context.
     *
     * @return target type class
     */
    @NonNull
    Class<?> getTargetClass();

    Options<TArtObject> info();

    /**
     * Checks if the target type matches the given object.
     *
     * @param object target to check against this context
     * @return true if the type matches or false of the object is null
     *          or does not extend the target type
     */
    boolean isTargetType(@Nullable Object object);

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
    <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value);

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
    <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass);
}