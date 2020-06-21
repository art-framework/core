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
import net.silthus.art.api.parser.ArtParser;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;

public class AbstractFactoryManager<TFactory extends ArtFactory<?, ?, ?, ?>> {

    protected final Map<String, Provider<ArtParser>> parser;
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, TFactory> factories = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PACKAGE)
    private Logger logger;

    public AbstractFactoryManager(Map<String, Provider<ArtParser>> parser) {
        this.parser = parser;
    }

    public boolean exists(String identifier) {
        return factories.containsKey(identifier);
    }

    public void register(Map<String, TFactory> factories) {
        for (Map.Entry<String, TFactory> entry : factories.entrySet()) {
            if (exists(entry.getKey())) {
                getLogger().warning("duplicate art factory detected for identifier " + entry.getKey() + ": " + entry.getValue().getArtObject().getClass().getCanonicalName() + " <--> " + factories.get(entry.getKey()).getArtObject().getClass().getCanonicalName());
                getLogger().warning("not registering: " + entry.getValue().getArtObject().getClass().getCanonicalName());
            } else {
                this.factories.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public Optional<TFactory> getFactory(String identifier) {
        return Optional.ofNullable(factories.get(identifier));
    }
}
