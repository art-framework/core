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
import net.silthus.art.conf.ActionConfig;

import java.util.Map;

public class DefaultActionFactory<TTarget> extends AbstractArtFactory<TTarget, ActionContext<TTarget>, Action<TTarget>> implements ActionFactory<TTarget> {

    public DefaultActionFactory(
            @NonNull Configuration configuration,
            @NonNull Class<TTarget> targetClass,
            @NonNull Class<Action<TTarget>> actionClass
    ) {
        super(configuration, targetClass, actionClass);
    }

    public DefaultActionFactory(
            @NonNull Configuration configuration,
            @NonNull Class<TTarget> targetClass,
            @NonNull Class<Action<TTarget>> actionClass,
            @NonNull ArtObjectProvider<Action<TTarget>> artObjectProvider
    ) {
        super(configuration, targetClass, actionClass, artObjectProvider);
    }

    @Override
    public ActionContext<TTarget> create(Map<ConfigMapType, ConfigMap> configMaps) {
        ActionConfig actionConfig = new ActionConfig();
        if (configMaps.containsKey(ConfigMapType.ART_CONFIG)) {
            actionConfig = configMaps.get(ConfigMapType.ART_CONFIG).applyTo(actionConfig);
        }
        return ActionContext.of(
                configuration(),
                getTargetClass(),
                createArtObject(configMaps.get(ConfigMapType.ART_OBJECT_CONFIG)),
                actionConfig
        );
    }
}
