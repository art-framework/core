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

import io.artframework.parser.flow.ConfigMapType;
import lombok.NonNull;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractArtFactory<TContext extends ArtObjectContext<TArtObject>, TArtObject extends ArtObject> implements ArtFactory<TContext, TArtObject> {

    private final Configuration configuration;
    private final Options<TArtObject> information;

    protected AbstractArtFactory(@NonNull Configuration configuration, @NonNull Options<TArtObject> information) {
        this.configuration = configuration;
        this.information = information;
    }

    @Override
    public Options<TArtObject> options() {
        return information;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final TArtObject createArtObject(ConfigMap configMap) {
        TArtObject artObject = options().provider().create();

        if (configMap == null || !configMap.isLoaded() || configMap.getType() != ConfigMapType.SPECIFIC_ART_CONFIG) {
            return artObject;
        }

        options().configClass().ifPresent(configClass -> {
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
