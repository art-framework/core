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

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class ConfiguredTriggerTarget<TTarget, TConfig> extends TriggerTarget<TTarget> {
    
    ConfigMap configMap;
    Class<TConfig> configClass;
    TriggerRequirement<TTarget, TConfig> requirement;

    public ConfiguredTriggerTarget(
            @NonNull Target<TTarget> target,
            @NonNull ConfigMap configMap,
            @NonNull Class<TConfig> configClass,
            @NonNull TriggerRequirement<TTarget, TConfig> requirement
    ) {
        super(target);
        this.configMap = configMap;
        this.configClass = configClass;
        this.requirement = requirement;
    }

    public Result test(ExecutionContext<TriggerContext> executionContext, TConfig config) {

        return requirement.test(target(), executionContext, config);
    }
}
