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

package io.artframework.conf;

import io.artframework.ConfigMap;
import io.artframework.ConfigurationException;
import io.artframework.Scope;
import io.artframework.annotations.ConfigOption;
import io.artframework.util.ConfigUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;

/**
 * The requirement config holds general information about the execution
 * properties of the requirement.
 * <p>
 * You can use the builder to create a new action config in a programmatic way.
 * Otherwise the config will be parsed by the given parser and provided for you.
 */
@Data
@ConfigOption
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RequirementConfig extends ArtObjectConfig {

    private static final long serialVersionUID = 2530536893768L;

    private static ConfigMap configMap;

    public static ConfigMap getConfigMap() {
        if (configMap == null) {
            try {
                configMap = ConfigMap.of(ConfigUtil.getConfigFields(RequirementConfig.class, new RequirementConfig()));
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return configMap;
    }

    public static RequirementConfig of(Scope scope, @Nullable ConfigMap configMap) {
        RequirementConfig config = new RequirementConfig();

        if (configMap == null) {
            return config;
        }

        return configMap.applyTo(scope, config);
    }

    @ConfigOption(description = {
            "Setting a count for the requirement will let it only become true if it was true for the number of the defined count.",
            "e.g. setting a count of 5 means the requirement must be met 5 times before it comes true"
    })
    protected int count;

    @ConfigOption(description = "Set to true if the outcome of the requirement should be negated/switched.")
    protected boolean negated;

    @ConfigOption(description = {
            "Set to true if you only want to check the requirement once and store the result afterwards.",
            "This will have the effect that a requirement that was true once will always be true in the future."
    })
    protected boolean checkOnce;
}

