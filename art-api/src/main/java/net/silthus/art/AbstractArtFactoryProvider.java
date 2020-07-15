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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.ArtFactory;
import net.silthus.art.ArtFactoryProvider;
import net.silthus.art.Configuration;
import net.silthus.art.api.ArtRegistrationException;

import javax.inject.Inject;
import java.util.*;
import java.util.logging.Logger;

public abstract class AbstractArtFactoryProvider<TFactory extends ArtFactory<?, ?>> implements ArtFactoryProvider<TFactory> {

    private final Configuration configuration;
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, TFactory> factories = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, String> aliasMappings = new HashMap<>();

    protected AbstractArtFactoryProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    public boolean exists(String identifier) {
        return factories.containsKey(identifier);
    }

    @Override
    public ArtFactoryProvider<TFactory> add(Collection<TFactory> factories) {
        factories.forEach(this::add);
        return this;
    }

    @Override
    public ArtFactoryProvider<TFactory> add(TFactory factory) {

        try {
            factory.initialize();

            String identifier = factory.getIdentifier();

            if (exists(identifier)) {
                throw new ArtRegistrationException("Duplicate ArtFactory for identifier \"" + identifier + "\" found. " +
                        "Tried to register " + factory.getArtObjectClass().getCanonicalName()
                        + " but already found " + factories.get(identifier).getArtObjectClass().getCanonicalName());
            } else {
                factories.put(identifier, factory);
                for (String alias : factory.getAlias()) {
                    if (!aliasMappings.containsKey(alias)) {
                        aliasMappings.put(alias, identifier);
                    } else {
//                        getLogger().warning("No registering duplicate alias " + alias + " of " + identifier + ". "
//                                + aliasMappings.get(alias) + " has already registered the alias.");
                    }
                }
            }
        } catch (ArtRegistrationException e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public Optional<TFactory> get(String identifier) {

        if (!factories.containsKey(identifier) && aliasMappings.containsKey(identifier)) {
            identifier = aliasMappings.get(identifier);
        }

        return Optional.ofNullable(factories.get(identifier));
    }
}
