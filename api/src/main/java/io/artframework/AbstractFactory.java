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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.lang.reflect.InvocationTargetException;

@Getter
@Accessors(fluent = true)
public abstract class AbstractFactory<TContext extends ArtObjectContext<TArtObject>, TArtObject extends ArtObject> implements Factory<TContext, TArtObject> {

    private final Scope scope;
    private final ArtObjectMeta<TArtObject> meta;

    protected AbstractFactory(@NonNull Scope scope, @NonNull ArtObjectMeta<TArtObject> meta) {
        this.scope = scope;
        this.meta = meta;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public final TArtObject create(ConfigMap configMap) {
        TArtObject artObject = meta().provider().create();

        if (configMap == null || !configMap.loaded()) {
            return artObject;
        }

        meta().configClass().ifPresent(configClass -> {
            if (artObject instanceof Configurable) {
                if (configClass.isInstance(artObject)) {
                    configMap.applyTo(artObject);
                    ((Configurable<TArtObject>) artObject).load(artObject);
                } else {
                    try {
                        Object config = configClass.getConstructor().newInstance();
                        configMap.applyTo(config);
                        ((Configurable) artObject).load(config);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            } else if (configClass.isInstance(artObject)) {
                configMap.applyTo(artObject);
            }
        });

        return artObject;
    }
}
