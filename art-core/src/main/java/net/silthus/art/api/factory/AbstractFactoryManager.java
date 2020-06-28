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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.api.ArtRegistrationException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class AbstractFactoryManager<TFactory extends ArtFactory<?, ?, ?, ?>> implements ArtFactoryManager<TFactory> {

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, TFactory> factories = new HashMap<>();
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PACKAGE)
    private Logger logger;

    public boolean exists(String identifier) {
        return factories.containsKey(identifier);
    }

    @Override
    public void register(List<TFactory> factories) throws ArtRegistrationException {

        int failedRegistrations = 0;

        for (TFactory factory : factories) {
            try {
                register(factory);
            } catch (ArtRegistrationException e) {
                getLogger().warning(e.getMessage());
                failedRegistrations++;
            }
        }

        if (failedRegistrations > 0) {
            throw new ArtRegistrationException("Failed to register " + failedRegistrations + " ArtFactories.");
        }
    }

    @Override
    public void register(TFactory factory) throws ArtRegistrationException {

        factory.initialize();

        String identifier = factory.getIdentifier();

        if (exists(identifier)) {
            throw new ArtRegistrationException("Duplicate ArtFactory for identifier \"" + identifier + "\" found. " +
                    "Tried to register " + factory.getArtObject().getClass().getCanonicalName()
                    + " but already found " + factories.get(identifier).getArtObject().getClass().getCanonicalName());
        } else {
            factories.put(identifier, factory);
        }
    }

    public Optional<TFactory> getFactory(String identifier) {
        return Optional.ofNullable(factories.get(identifier));
    }
}
