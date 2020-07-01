package net.silthus.art.api.actions;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
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
 *
 * @param <TConfig> custom config type of the action
 */
@Setter(AccessLevel.PACKAGE)
@ConfigurationElement
@EqualsAndHashCode(callSuper = true)
public final class ActionConfig<TConfig> extends ArtObjectConfig<TConfig> {

    public static final Map<String, ConfigFieldInformation> CONFIG_FIELD_INFORMATION = new HashMap<>();

    static {
        try {
            CONFIG_FIELD_INFORMATION.putAll(ConfigUtil.getConfigFields(ActionConfig.class));
        } catch (ArtConfigException e) {
            e.printStackTrace();
        }
    }

    public ActionConfig() {
        super(null);
    }

    public ActionConfig(TConfig with) {
        super(with);
    }

    private String delay = "0s";
    private String cooldown = "0s";
    private boolean execute_once = false;

    /**
     * The delay in ticks for this action.
     *
     * @return delay in server ticks
     */
    public long getDelay() {
        return TimeUtil.parseTimeAsTicks(delay);
    }

    /**
     * The cooldown in ticks for this action.
     *
     * @return cooldown in ticks
     */
    public long getCooldown() {
        return TimeUtil.parseTimeAsTicks(cooldown);
    }

    public boolean isExecuteOnce() {
        return execute_once;
    }
}
