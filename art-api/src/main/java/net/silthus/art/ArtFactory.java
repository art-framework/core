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

import net.silthus.art.api.ArtObjectInformationException;

import java.util.HashMap;
import java.util.Map;

public interface ArtFactory<TContext extends ArtObjectContext, TArtObject extends ArtObject> extends Provider {

    ArtObjectInformation<TArtObject> info();

    /**
     * Initializes the {@link ArtFactory}, loads all annotations and checks
     * if the {@link ArtObject} is configured correctly.
     * <br>
     * If everything looks good the {@link ArtObject} is registered for execution.
     * If not a {@link ArtObjectInformationException} is thrown.
     *
     * @throws ArtObjectInformationException if the {@link ArtObject} could not be registered.
     */
    void initialize() throws ArtObjectInformationException;

    default TContext create() {
        return create(new HashMap<>());
    }

    TContext create(Map<ConfigMapType, ConfigMap> configMaps);
}
