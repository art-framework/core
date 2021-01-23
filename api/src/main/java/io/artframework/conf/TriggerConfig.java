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
import io.artframework.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;

@Data
@ConfigOption
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class TriggerConfig extends RequirementConfig {

    private static ConfigMap configMap;

    public static ConfigMap configMap() {
        if (configMap == null) {
            try {
                configMap = ConfigMap.of(ConfigUtil.getConfigFields(TriggerConfig.class, new TriggerConfig()));
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return configMap;
    }

    public static TriggerConfig of(Scope scope, @Nullable ConfigMap configMap) {
        TriggerConfig config = new TriggerConfig();

        if (configMap == null) {
            return config;
        }

        return configMap.applyTo(scope, config);
    }

    @ConfigOption(description = {
            "Delay of the trigger,",
            "Use the 'time' (e.g.: 1h20s) annotation to specify the delay this trigger has.",
            "Delay means the time to wait before executing any actions and informing others about the execution of this trigger."
    })
    private String delay = "0s";

    @ConfigOption(description = {
            "Cooldown of the trigger.",
            "Use the 'time' (e.g.: 1h20s) annotation to specify the cooldown this trigger has.",
            "Cooldown means the time between executions."
    })
    private String cooldown = "0s";

    @ConfigOption(description = "Set this to true to execute this trigger only once.")
    private boolean executeOnce;

    @ConfigOption(description = {
            "Set this to false to prevent any actions being executed by this trigger.",
            "Any listeners will still be informed and all requirements checked."
    })
    private boolean executeActions = true;

    /**
     * Gets the delay of this trigger measured in ticks.
     * 20 ticks are 1 second.
     *
     * @return delay in ticks
     */
    public long delay() {
        return TimeUtil.parseTimeAsMilliseconds(delay);
    }

    /**
     * Gets the cooldown of this trigger in milliseconds.
     *
     * @return cooldown in milliseconds
     */
    public long cooldown() {
        return TimeUtil.parseTimeAsMilliseconds(cooldown);
    }
}
