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

/**
 * Provides a way to create different {@link Target} implementations
 * for different target types.
 * Register your {@link Target} and {@link TargetProvider} in the {@link Configuration}
 * by calling {@link Configuration#set(Class, TargetProvider)}.
 *
 * @param <TTarget> type of the target source
 * @see Target
 */
public interface TargetProvider<TTarget> {

    /**
     * Creates a new {@link Target} that wraps the given target source.
     *
     * @param target target to wrap
     * @return wrapped {@link Target}
     */
    Target<TTarget> create(@NonNull TTarget target);
}
