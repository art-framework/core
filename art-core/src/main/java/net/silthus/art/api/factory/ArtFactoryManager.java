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

package net.silthus.art.api.factory;

import net.silthus.art.ArtObject;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtRegistrationException;

import java.util.Optional;

public interface ArtFactoryManager<TFactory extends ArtFactory<? ,? ,?, ?>> extends ArtFactoryRegistration<TFactory> {
    /**
     * Checks if a {@link ArtFactory} exists for the given identifier.
     *
     * @param identifier identifier of the {@link ArtObject}
     * @return true if an {@link ArtFactory} exists, false otherwise
     */
    boolean exists(String identifier);

    /**
     * Registers the given factory calling {@link ArtFactory#initialize()} on it
     * and if successful putting it into the factory store.
     * Registration will fail if an other factory with the same identifier exists
     * or if the factory does not provide the necessary annotations.
     *
     * @param factory factory to register
     * @throws ArtRegistrationException if a duplicate factory exists, or the initialization of the factory failed
     */
    void register(TFactory factory) throws ArtRegistrationException;

    /**
     * Tries to find a matching {@link ArtFactory} for the given identifier.
     * Use the factory to crate instances of the {@link ArtObject} wrapped as {@link ArtContext}.
     * <br>
     * Returns {@link Optional#empty()} if no {@link ArtObject} with a matching identifier is found.
     *
     * @param identifier identifier of the {@link ArtObject}
     * @return matching {@link ArtFactory} or an empty {@link Optional} if no {@link ArtObject} with the identifier was found
     * @see ArtFactory
     */
    Optional<TFactory> getFactory(String identifier);
}
