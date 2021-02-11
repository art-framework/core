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
import io.artframework.util.ConfigUtil;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.Objects;

@Log(topic = "art-framework")
public class DefaultActionProvider extends AbstractFactoryProvider<ActionFactory<?>> implements ActionProvider {

    public DefaultActionProvider(Scope scope) {
        super(scope);
    }

    @Override
    public ActionProvider add(@NonNull ArtObjectMeta<Action<?>> actionInformation) {
        addFactory(ActionFactory.of(scope(), actionInformation.get()));
        log.info("[REGISTERED] !" + actionInformation.identifier() + " " + ConfigUtil.toConfigString(actionInformation.configMap()));
        return this;
    }

    @Override
    public ActionProvider add(@NonNull String identifier, @NonNull GenericAction action) {
        return add(ArtObjectMeta.of(identifier, Object.class, action));
    }

    @Override
    public <TTarget> ActionProvider add(String identifier, Class<TTarget> targetClass, Action<TTarget> action) {
        return add(ArtObjectMeta.of(identifier, targetClass, action));
    }

    public ActionProvider add(@NonNull Class<? extends Action<?>> aClass) {
        try {
            return add(Objects.requireNonNull(ArtObjectMeta.of(scope(), aClass).get()));
        } catch (ArtMetaDataException e) {
            log.warning("failed to add " + aClass.getCanonicalName() + ": " + e.error().message());
        }
        return this;
    }

    public <TAction extends Action<TTarget>, TTarget> ActionProvider add(Class<TAction> aClass, ArtObjectProvider<TAction> artObjectProvider) {
        try {
            return add(Objects.requireNonNull(ArtObjectMeta.of(scope(), aClass, artObjectProvider).get()));
        } catch (ArtMetaDataException e) {
            log.warning("failed to add " + aClass.getCanonicalName() + ": " + e.error().message());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TAction extends Action<TTarget>, TTarget> ActionProvider add(ArtObjectProvider<TAction> action) {

        return add((Class<TAction>) action.create().getClass(), action);
    }

    public ActionProvider addAll(Collection<ArtObjectMeta<?>> artObjects) {
        for (ArtObjectMeta<?> artObject : artObjects) {
            add(Objects.requireNonNull(artObject.get()));
        }
        return this;
    }
}
