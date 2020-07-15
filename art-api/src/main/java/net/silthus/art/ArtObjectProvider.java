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

/**
 * Provides a way to create new instances of the given {@link ArtObject} type.
 *
 * @param <TArtObject> type of the {@link ArtObject}
 */
@FunctionalInterface
public interface ArtObjectProvider<TArtObject extends ArtObject> {

    /**
     * Creates a new fresh instance of the given {@link ArtObject}.
     * This must not be a singleton or cached. Create a new instance for every call.
     *
     * @return created {@link ArtObject}
     */
    TArtObject create();
}
