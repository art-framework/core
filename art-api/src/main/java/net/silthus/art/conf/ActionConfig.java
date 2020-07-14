package net.silthus.art.conf;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.ConfigOption;
import net.silthus.art.api.config.ArtConfigException;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.util.ConfigUtil;
import net.silthus.art.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link ActionConfig} holds general information about the execution
 * properties of the action. Like delay, cooldown, etc.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
public final class ActionConfig extends ArtObjectConfig {

    public static final Map<String, ConfigFieldInformation> CONFIG_FIELD_INFORMATION = new HashMap<>();

    static {
        try {
            CONFIG_FIELD_INFORMATION.putAll(ConfigUtil.getConfigFields(ActionConfig.class));
        } catch (ArtConfigException e) {
            e.printStackTrace();
        }
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
        return TimeUtil.parseTimeAsMillis(delay);
    }

    /**
     * The cooldown in milliseconds for this action.
     *
     * @return cooldown in milliseconds
     */
    public long getCooldown() {
        return TimeUtil.parseTimeAsMillis(cooldown);
    }
}
