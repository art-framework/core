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

import lombok.NonNull;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.util.function.Function;

/**
 * The {@link Target} is a wrapper around the different target types used by all ART objects.
 * It is needed to provide a consistent way to get a unique identifier
 * for every target type. Target types could be players, entities, chests, etc.
 * <br>
 * It is recommended that you create your own target types by extending {@link AbstractTarget}
 * which already has the correct equals and hashcode implementation.
 * Make sure that your equal and hashcode is only scoped to the {@link #getUniqueId()} method,
 * if you directly implement the {@link Target}.
 * <br>
 * Register your target type with the {@link Configuration} on startup by calling
 * {@link Configuration#target(Class, Function)} and provide a function
 * that is used as a factory to create new {@link Target} instances from the given source type.
 * <br>
 * You can also extend your {@link Target} by implementing one or more of the following interfaces:
 *      - {@link MessageSender}: allows your target to receive messages
 * <br>
 * All {@link Target} implementations must be immutable and the source and unique id must not change.
 *
 * @param <TTarget> type of the underlying target
 */
@Immutable
public interface Target<TTarget> {

    /**
     * Wraps the given target object into a {@link Target}.
     * It will try to find the best possible (nearest) wrapper
     * and will return null if no wrapper was found.
     * <br>
     * Delegates to {@link TargetProvider#get(Object)}.
     *
     * @param target    target to wrap
     * @param <TTarget> type of the target
     * @return wrapped target object or null if no wrapper was found
     * @see TargetProvider#get(Object)
     */
    static <TTarget> Optional<Target<TTarget>> of(@NonNull TTarget target) {
        return ART.getTarget(target);
    }

    /**
     * Gets a unique identifier of the wrapped target object.
     * The identifier must be consistent across instances of the same object.
     * <br>
     * For example: the same player should always have the same unique identifier.
     * For a player this is easy, just use the Player#getUniqueId().toString() method.
     * <br>
     * If you want to target more abstract objects, e.g. a chest, then you need to compose
     * a consistent unique identifier yourself. This could be the location of the chest or
     * the unique id of the inventory holder paired with the location and chest type.
     *
     * @return The unique identifier of the target. This is never null or empty.
     */
    String getUniqueId();

    /**
     * @return The underlying target source. This is never null.
     */
    TTarget getSource();

    default boolean isTargetType(Class<?> targetClass) {
        return targetClass.isInstance(getSource());
    }
}
