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
import io.artframework.annotations.ConfigOption;
import io.artframework.util.ConfigUtil;
import io.artframework.util.TimeUtil;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;

/**
 * The action config holds general information about the execution
 * properties of the action. Like delay, cooldown, etc.
 * <p>
 * You can use the builder to create a new action config in a programmatic way.
 * Otherwise the config will be parsed by the given parser and provided for you.
 */
@Data
@ConfigOption
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public final class ActionConfig extends ArtObjectConfig {

    private static final long serialVersionUID = 94782492952L;

    private static ConfigMap configMap;

    public static ConfigMap configMap() {
        if (configMap == null) {
            try {
                configMap = ConfigMap.of(ConfigUtil.getConfigFields(ActionConfig.class, new ActionConfig()));
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return configMap;
    }

    public static ActionConfig of(@Nullable ConfigMap configMap) {
        ActionConfig config = new ActionConfig();

        if (configMap == null) {
            return config;
        }

        return configMap.applyTo(config);
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
    private boolean executeOnce;

    /**
     * The delay in milliseconds for this action.
     *
     * @return delay in milliseconds
     */
    public long delay() {
        return TimeUtil.parseTimeAsMilliseconds(delay);
    }

    /**
     * The cooldown in milliseconds for this action.
     *
     * @return cooldown in milliseconds
     */
    public long cooldown() {
        return TimeUtil.parseTimeAsMilliseconds(cooldown);
    }
}
