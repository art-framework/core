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

package net.silthus.art.conf;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.ArtConfigException;
import net.silthus.art.ConfigMap;
import net.silthus.art.ConfigOption;
import net.silthus.art.Requirement;
import net.silthus.art.parser.flow.ConfigMapType;
import net.silthus.art.util.ConfigUtil;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@link RequirementConfig} holds general information about the execution
 * properties of the requirement.
 * The config will be created when the underlying {@link Requirement} is created.
 */
@Immutable
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
public class RequirementConfig extends ArtObjectConfig {

    private static final long serialVersionUID = 2530536893768L;

    public static final Map<String, ConfigFieldInformation> CONFIG_FIELD_INFORMATION = new HashMap<>();
    public static final ConfigMap CONFIG_MAP = ConfigMap.of(ConfigMapType.GENERAL_ART_CONFIG, CONFIG_FIELD_INFORMATION);

    static {
        try {
            CONFIG_FIELD_INFORMATION.putAll(ConfigUtil.getConfigFields(RequirementConfig.class));
        } catch (ArtConfigException e) {
            e.printStackTrace();
        }
    }

    @ConfigOption(description = {
            "Setting a count for the requirement will let it only become true if it was true for the number of the defined count.",
            "e.g. setting a count of 5 means the requirement must be met 5 times before it comes true"
    })
    private int count = 0;

    @ConfigOption(description = "Set to true if the outcome of the requirement should be negated/switched.")
    private boolean negated;

    @ConfigOption(description = {
            "Set to true if you only want to check the requirement once and store the result afterwards.",
            "This will have the effect that a requirement that was true once will always be true in the future."
    })
    private boolean checkOnce;
}

