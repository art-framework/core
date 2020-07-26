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

import io.artframework.*;
import lombok.NonNull;

import java.util.Collection;
import java.util.Objects;

public class DefaultActionProvider extends AbstractArtFactoryProvider<ActionFactory<?>> implements ActionProvider {

    public DefaultActionProvider(Configuration configuration) {
        super(configuration);
    }

    @Override
    public ActionProvider add(@NonNull ArtInformation<Action<?>> actionInformation) {
        addFactory(ActionFactory.of(getConfiguration(), actionInformation.get()));
        return this;
    }

    @Override
    public ActionProvider add(@NonNull String identifier, @NonNull GenericAction action) {
        return add(ArtInformation.of(identifier, Object.class, action));
    }

    @Override
    public <TTarget> ActionProvider add(String identifier, Class<TTarget> targetClass, Action<TTarget> action) {
        return add(ArtInformation.of(identifier, targetClass, action));
    }

    public ActionProvider add(@NonNull Class<? extends Action<?>> aClass) {
        try {
            return add(Objects.requireNonNull(ArtInformation.of(aClass).get()));
        } catch (ArtObjectInformationException e) {
            // TODO: error handling
            e.printStackTrace();
        }
        return this;
    }

    public <TAction extends Action<TTarget>, TTarget> ActionProvider add(Class<TAction> aClass, ArtObjectProvider<TAction> artObjectProvider) {
        try {
            return add(Objects.requireNonNull(ArtInformation.of(aClass, artObjectProvider).get()));
        } catch (ArtObjectInformationException e) {
            // TODO: error handling
            e.printStackTrace();
        }
        return this;
    }

    public ActionProvider addAll(Collection<ArtInformation<?>> artObjects) {
        for (ArtInformation<?> artObject : artObjects) {
            add(Objects.requireNonNull(artObject.get()));
        }
        return this;
    }
}
