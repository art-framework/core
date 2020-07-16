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

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link ArtFactory} is used to create new instances of {@link ArtObject}s which
 * are wrapped inside an {@link ArtObjectContext}.
 * The created context and art object is initialized with its configuration and properties.
 * <br>
 * Every {@link ArtObject} must have exactly one {@link ArtObjectContext} and an {@link ArtFactory}
 * that knows how to create the context, a new instance of the art object and how to load the configuration
 * of the art object.
 *
 * @param <TContext>
 * @param <TArtObject>
 */
public interface ArtFactory<TContext extends ArtObjectContext, TArtObject extends ArtObject> extends Provider {

    static <TTarget> ArtFactory<ActionContext<TTarget>, Action<TTarget>> ofAction(
            @NonNull Configuration configuration,
            @NonNull ArtObjectInformation<Action<TTarget>> information
    ) {
        return ActionFactory.of(configuration, information);
    }

    static <TTarget> ArtFactory<RequirementContext<TTarget>, Requirement<TTarget>> ofRequirement(
            @NonNull Configuration configuration,
            @NonNull ArtObjectInformation<Requirement<TTarget>> information
    ) {
        return RequirementFactory.of(configuration, information);
    }

    static ArtFactory<TriggerContext, Trigger> ofTrigger(
            @NonNull Configuration configuration,
            @NonNull ArtObjectInformation<Trigger> information
    ) {
        return TriggerFactory.of(configuration, information);
    }

    ArtObjectInformation<TArtObject> info();

    default TContext create() {
        return create(new HashMap<>());
    }

    TContext create(Map<ConfigMapType, ConfigMap> configMaps);
}
