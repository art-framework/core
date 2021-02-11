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

package io.artframework.impl;

import io.artframework.AbstractFactoryProvider;
import io.artframework.ArtMetaDataException;
import io.artframework.ArtObjectMeta;
import io.artframework.ArtObjectProvider;
import io.artframework.GenericRequirement;
import io.artframework.Requirement;
import io.artframework.RequirementFactory;
import io.artframework.RequirementProvider;
import io.artframework.Scope;
import io.artframework.util.ConfigUtil;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.Objects;

@Log(topic = "art-framework")
public class DefaultRequirementProvider extends AbstractFactoryProvider<RequirementFactory<?>> implements RequirementProvider {

    public DefaultRequirementProvider(Scope scope) {
        super(scope);
    }

    @Override
    public RequirementProvider add(@NonNull ArtObjectMeta<Requirement<?>> information) {
        addFactory(RequirementFactory.of(scope(), information.get()));
        log.info("[REGISTERED] ?" + information.identifier()+ " " + ConfigUtil.toConfigString(information.configMap()));
        return this;
    }

    @Override
    public RequirementProvider add(@NonNull String identifier, @NonNull GenericRequirement action) {
        return add(ArtObjectMeta.of(identifier, Object.class, action));
    }

    @Override
    public <TTarget> RequirementProvider add(String identifier, Class<TTarget> targetClass, Requirement<TTarget> action) {
        return add(ArtObjectMeta.of(identifier, targetClass, action));
    }

    public RequirementProvider add(@NonNull Class<? extends Requirement<?>> aClass) {
        try {
            return add(Objects.requireNonNull(ArtObjectMeta.of(scope(), aClass).get()));
        } catch (ArtMetaDataException e) {
            log.warning("failed to add " + aClass.getCanonicalName() + ": " + e.error().message());
        }
        return this;
    }

    public <TRequirement extends Requirement<TTarget>, TTarget> RequirementProvider add(Class<TRequirement> aClass, ArtObjectProvider<TRequirement> artObjectProvider) {
        try {
            return add(Objects.requireNonNull(ArtObjectMeta.of(scope(), aClass, artObjectProvider).get()));
        } catch (ArtMetaDataException e) {
            log.warning("failed to add " + aClass.getCanonicalName() + ": " + e.error().message());
        }
        return this;
    }

    public RequirementProvider addAll(Collection<ArtObjectMeta<?>> artObjects) {
        for (ArtObjectMeta<?> artObject : artObjects) {
            add(Objects.requireNonNull(artObject.get()));
        }
        return this;
    }
}
