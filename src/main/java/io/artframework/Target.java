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

/**
 * The {@link Target} is a wrapper around the different target types used by all ART objects.
 * It is needed to provide a consistent way to get a unique identifier
 * for every target type. Target types could be players, entities, chests, etc.
 * <p>
 * It is recommended that you create your own target types by extending {@link AbstractTarget}
 * which already has the correct equals and hashcode implementation.
 * Make sure that your equal and hashcode is only scoped to the {@link #uniqueId()} method,
 * if you directly implement the {@link Target}.
 * <p>
 * Register your target type with the {@link Configuration} on startup by calling
 * {@link Configuration#targets()} and provide a function that is used as a factory
 * to create new {@link Target} instances from the given source type.
 * <p>
 * You can also extend your {@link Target} by implementing one or more of the following interfaces:
 *      - {@link MessageSender}: allows your target to receive messages
 * <p>
 * All {@link Target} implementations must be immutable and the source and unique id must not change.
 *
 * @param <TTarget> type of the underlying target
 */
public interface Target<TTarget> extends ArtObject {

    /**
     * Gets a unique identifier of the wrapped target object.
     * The identifier must be consistent across instances of the same object.
     * <p>
     * For example: the same player should always have the same unique identifier.
     * For a player this is easy, just use the Player#getUniqueId().toString() method.
     * <p>
     * If you want to target more abstract objects, e.g. a chest, then you need to compose
     * a consistent unique identifier yourself. This could be the location of the chest or
     * the unique id of the inventory holder paired with the location and chest type.
     *
     * @return The unique identifier of the target. This is never null or empty.
     */
    String uniqueId();

    /**
     * @return The underlying target source. This is never null.
     */
    TTarget source();

    /**
     * Checks if the given class is the same as the target class or a superclass of the target class.
     * <p>
     * If you pass <code>Object</code> as the target type then this will always return true.
     *
     * @param targetClass the target class to check against the target source
     * @return true if the target source is of the given target class
     * @see Class#isInstance(Object)
     */
    default boolean isTargetType(Class<?> targetClass) {
        return targetClass.isInstance(source());
    }
}
