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

package net.silthus.art.api.trigger;

import lombok.NonNull;
import net.silthus.art.ART;
import net.silthus.art.ArtBuilder;
import net.silthus.art.ArtModuleDescription;
import net.silthus.art.api.Trigger;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The {@link Target} is a wrapper around your {@link Trigger} targets.
 * It is needed to provide a way to uniquely identify the target,
 * which is needed to store meta data information and cache results.
 * <br>
 * Register your wrapper with the {@link ArtBuilder} on startup by calling
 * {@link ART#register(ArtModuleDescription, Consumer)} and then {@link ArtBuilder.TargetBuilder#wrapper(Function)}.
 *
 * @param <TTarget> type of the underlying target
 */
public interface Target<TTarget> {

    /**
     * Wraps the given target object into a {@link Target}.
     * It will try to find the best possible (nearest) wrapper
     * and will return null if no wrapper was found.
     *
     * @param target target to wrap
     * @param <TTarget> type of the target
     * @return wrapped target object or null if no wrapper was found
     */
    @Nullable
    static <TTarget> Target<TTarget> of(@NonNull TTarget target) {
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
     * @return unique identifier
     */
    String getUniqueId();

    /**
     * @return wrapped target object
     */
    @NonNull
    TTarget getTarget();
}
