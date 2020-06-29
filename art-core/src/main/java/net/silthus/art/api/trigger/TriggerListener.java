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
import net.silthus.art.api.Trigger;
import net.silthus.art.api.parser.ArtResult;

/**
 * Used to listen on events fired by {@link Trigger}s.
 * <p>
 * Make sure to register your listener either with the {@link ArtResult#onTrigger(Class, TriggerListener)}
 * or {@link TriggerManager#addListener(String, Class, TriggerListener)} method.
 *
 * @param <TTarget> target type
 */
@FunctionalInterface
public interface TriggerListener<TTarget> {

    /**
     * Gets called when a trigger got executed and informs its listeners.
     *
     * You can use the {@link Target#getUniqueId()} to get a unique
     * non changing identifier of the trigger target that can be used
     * to cache or reference the target.
     *
     * @param target wrapped {@link Target} of the trigger
     */
    void onTrigger(@NonNull Target<TTarget> target);
}
