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

import net.silthus.art.api.ArtRegistrationException;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;

import java.util.List;

@FunctionalInterface
public interface ArtFactoryRegistration<TFactory extends ArtFactory<? ,? ,?, ?>> {

    /**
     * Registers the given {@link ActionFactory} types with the {@link ActionManager}.
     * This should happen whenever a plugin registers their ART.
     * An {@link ArtRegistrationException} will be thrown if any of the factories in the list fail to register.
     * The passed {@link List} will be modified and any factories failed to register will be removed from it.
     * You can also use the {@link ArtFactoryManager#register(ArtFactory)} method for a more controlled registration.
     *
     * @param factories list of factories to register
     * @throws ArtRegistrationException if {@link ArtFactory#initialize()} failed or a duplicate factory exists
     * @see ArtFactoryManager#register(ArtFactory)
     */
    void register(List<TFactory> factories) throws ArtRegistrationException;
}
