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

package io.artframework;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractFactoryProvider<TFactory extends Factory<?, ?>> implements FactoryProvider<TFactory> {

    private final Scope scope;
    final Map<String, TFactory> factories = new HashMap<>();
    final Map<String, String> aliasMappings = new HashMap<>();

    protected AbstractFactoryProvider(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope scope() {
        return scope;
    }

    public boolean exists(@NonNull String identifier) {
        identifier = identifier.toLowerCase();

        return factories.containsKey(identifier) || aliasMappings.containsKey(identifier.toLowerCase());
    }

    @Override
    public Map<String, TFactory> all() {
        return ImmutableMap.copyOf(factories);
    }

    @Override
    public Map<String, String> aliasMappings() {
        return ImmutableMap.copyOf(aliasMappings);
    }

    @Override
    public Optional<TFactory> get(String identifier) {

        if (Strings.isNullOrEmpty(identifier)) return Optional.empty();

        identifier = identifier.toLowerCase();

        if (!factories.containsKey(identifier) && aliasMappings.containsKey(identifier)) {
            identifier = aliasMappings.get(identifier);
        }

        return Optional.ofNullable(factories.get(identifier));
    }

    protected void addFactory(@NonNull TFactory factory) {
        String identifier = factory.meta().identifier();
        factories.put(identifier.toLowerCase(), factory);
        for (String alias : factory.meta().alias()) {
            if (!aliasMappings.containsKey(alias)) {
                aliasMappings.put(alias, identifier);
            }
        }
    }

    public FactoryProvider<TFactory> remove(String identifier) {

        factories.remove(identifier);
        aliasMappings.remove(identifier);
        return this;
    }

    public FactoryProvider<TFactory> remove(Class<? extends ArtObject> artClass) {

        try {
            remove(ArtObjectMeta.of(artClass).identifier());
        } catch (ArtMetaDataException ignored) {
        }
        return this;
    }
}
