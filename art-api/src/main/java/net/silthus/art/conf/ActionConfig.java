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
import net.silthus.art.parser.flow.ConfigMapType;
import net.silthus.art.util.ConfigUtil;
import net.silthus.art.util.TimeUtil;

/**
 * The {@link ActionConfig} holds general information about the execution
 * properties of the action. Like delay, cooldown, etc.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
public final class ActionConfig extends ArtObjectConfig {

    private static final long serialVersionUID = 94782492952L;

    private static ConfigMap configMap;

    public static ConfigMap getConfigMap() {
        if (configMap == null) {
            try {
                configMap = ConfigMap.of(ConfigMapType.GENERAL_ART_CONFIG, ConfigUtil.getConfigFields(ActionConfig.class));
            } catch (ArtConfigException e) {
                e.printStackTrace();
            }
        }
        return configMap;
    }

    @ConfigOption(description = {
            "The delay after which the action is executed.",
            TimeUtil.TIME_DESC
    })
    private String delay = "0s";

    @ConfigOption(description = {
            "Prevents a consecutive execution of this action before the cooldown ended.",
            TimeUtil.TIME_DESC
    })
    private String cooldown = "0s";

    @ConfigOption(description = "Will only execute the action once.")
    private boolean executeOnce = false;

    /**
     * The delay in milliseconds for this action.
     *
     * @return delay in milliseconds
     */
    public long getDelay() {
        return TimeUtil.parseTimeAsMilliseconds(delay);
    }

    /**
     * The cooldown in milliseconds for this action.
     *
     * @return cooldown in milliseconds
     */
    public long getCooldown() {
        return TimeUtil.parseTimeAsMilliseconds(cooldown);
    }
}
