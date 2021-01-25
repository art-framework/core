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

import io.artframework.conf.ContextConfig;

/**
 * The {@link Factory} is used to create new instances of {@link ArtObject}s which
 * are wrapped inside an {@link ArtObjectContext}.
 * The created context and art object is initialized with its configuration and properties.
 * <p>
 * Every {@link ArtObject} must have exactly one {@link ArtObjectContext} and an {@link Factory}
 * that knows how to create the context, a new instance of the art object and how to load the configuration
 * of the art object.
 *
 * @param <TContext> the type of the context that is created for the art object by the factory
 * @param <TArtObject> the art object type that is created by the factory, e.g. {@link Action}
 */
public interface Factory<TContext extends ArtObjectContext<TArtObject>, TArtObject extends ArtObject> extends Scoped {

    /**
     * @return the meta information of the art object created by this factory
     */
    ArtObjectMeta<TArtObject> meta();

    /**
     * Creates a new ArtObject instance provided by this factory with the given configuration.
     * <p>The created object will have its field injected that are defined in the provided {@link ConfigMap}.
     * <p>The object will be created automtically by the {@link ArtObjectContext} that is created
     * with the {@link #createContext(ContextConfig)} method.
     *
     * @param configMap the config map that holds the config information of the object
     * @return a new art object instance for the type of this factory
     */
    TArtObject create(ConfigMap configMap);

    /**
     * Creates a new ArtObjectContext that controls the execution flow of the ArtObject.
     * <p>The context will dynamically create new instances of the {@link ArtObject} using {@link #create(ConfigMap)}.
     *
     * @param config the configuration maps of the context
     * @return a new instance of the art object context defined as the context type of this factory
     */
    TContext createContext(ContextConfig config);
}
