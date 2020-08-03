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

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link Factory} is used to create new instances of {@link ArtObject}s which
 * are wrapped inside an {@link ArtObjectContext}.
 * The created context and art object is initialized with its configuration and properties.
 * <br>
 * Every {@link ArtObject} must have exactly one {@link ArtObjectContext} and an {@link Factory}
 * that knows how to create the context, a new instance of the art object and how to load the configuration
 * of the art object.
 *
 * @param <TContext>
 * @param <TArtObject>
 */
public interface Factory<TContext extends ArtObjectContext<TArtObject>, TArtObject extends ArtObject> extends Scope {

    static <TTarget> Factory<ActionContext<TTarget>, Action<TTarget>> ofAction(
            @NonNull Configuration configuration,
            @NonNull Options<Action<TTarget>> information
    ) {
        return ActionFactory.of(configuration, information);
    }

    static <TTarget> Factory<RequirementContext<TTarget>, Requirement<TTarget>> ofRequirement(
            @NonNull Configuration configuration,
            @NonNull Options<Requirement<TTarget>> information
    ) {
        return RequirementFactory.of(configuration, information);
    }

    static Factory<TriggerContext, Trigger> ofTrigger(
            @NonNull Configuration configuration,
            @NonNull Options<Trigger> information
    ) {
        return TriggerFactory.of(configuration, information);
    }

    Options<TArtObject> options();

    default TContext create() {
        return create(new HashMap<>());
    }

    TContext create(Map<ConfigMapType, ConfigMap> configMaps);
}
