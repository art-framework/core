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

import io.artframework.conf.ConfigFieldInformation;
import io.artframework.parser.flow.ConfigMapType;
import io.artframework.util.ConfigUtil;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

import java.util.Map;

@Value
@NonFinal
@Accessors(fluent = true)
public class TriggerTarget<TTarget> {

    Target<TTarget> target;

    public TriggerTarget(Target<TTarget> target) {
        this.target = target;
    }

    public <TConfig> TriggerTarget<TTarget> with(@NonNull Class<TConfig> configClass, @NonNull TriggerRequirement<TTarget, TConfig> requirement) {
        try {
            Map<String, ConfigFieldInformation> configFields = ConfigUtil.getConfigFields(configClass);
            ConfigMap configMap = ConfigMap.of(ConfigMapType.ART_CONFIG, configFields);

            return new ConfiguredTriggerTarget<>(target, configMap, configClass, requirement);
        } catch (ArtConfigException e) {
            e.printStackTrace();
            return this;
        }
    }
}
