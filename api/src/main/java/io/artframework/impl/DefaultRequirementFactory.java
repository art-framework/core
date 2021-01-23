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
import io.artframework.conf.RequirementConfig;
import lombok.NonNull;

public class DefaultRequirementFactory<TTarget> extends AbstractFactory<RequirementContext<TTarget>, Requirement<TTarget>> implements RequirementFactory<TTarget> {

    public DefaultRequirementFactory(
            @NonNull Scope scope,
            @NonNull ArtObjectMeta<Requirement<TTarget>> information
    ) {
        super(scope, information);
    }

    @Override
    public RequirementContext<TTarget> create(ConfigMap configMap, ConfigMap individualConfig) {

        return RequirementContext.of(
                scope(),
                meta(),
                createArtObject(individualConfig),
                RequirementConfig.of(scope(), configMap)
        );
    }
}
