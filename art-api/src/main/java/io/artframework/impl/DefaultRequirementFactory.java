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
import io.artframework.parser.flow.ConfigMapType;
import lombok.NonNull;

import java.util.Map;

public class DefaultRequirementFactory<TTarget> extends AbstractArtFactory<RequirementContext<TTarget>, Requirement<TTarget>> implements RequirementFactory<TTarget> {

    public DefaultRequirementFactory(
            @NonNull Configuration configuration,
            @NonNull Options<Requirement<TTarget>> information
    ) {
        super(configuration, information);
    }

    @Override
    public RequirementContext<TTarget> create(Map<ConfigMapType, ConfigMap> configMaps) {

        RequirementConfig config = RequirementConfig.builder().build();
        if (configMaps.containsKey(ConfigMapType.GENERAL_ART_CONFIG)) {
            config = configMaps.get(ConfigMapType.GENERAL_ART_CONFIG).applyTo(config);
        }

        return RequirementContext.of(
                getConfiguration(),
                options(),
                createArtObject(configMaps.get(ConfigMapType.SPECIFIC_ART_CONFIG)),
                config
        );
    }
}
