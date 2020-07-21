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

package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.conf.TriggerConfig;
import net.silthus.art.parser.flow.ConfigMapType;

import java.util.Map;

public class DefaultTriggerFactory extends AbstractArtFactory<TriggerContext, Trigger> implements TriggerFactory {

    public DefaultTriggerFactory(
            @NonNull Configuration configuration,
            @NonNull ArtInformation<Trigger> information
    ) {
        super(configuration, information);
    }

    @Override
    public TriggerContext create(Map<ConfigMapType, ConfigMap> configMaps) {

        TriggerConfig config = new TriggerConfig();
        if (configMaps.containsKey(ConfigMapType.GENERAL_ART_CONFIG)) {
            config = configMaps.get(ConfigMapType.GENERAL_ART_CONFIG).applyTo(config);
        }

        return TriggerContext.of(
                getConfiguration(),
                info(),
                config
        );
    }
}
