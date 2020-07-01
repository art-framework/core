package net.silthus.art.api.trigger;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.util.TimeUtil;

@Setter(AccessLevel.PACKAGE)
@ConfigurationElement
@EqualsAndHashCode(callSuper = true)
public class TriggerConfig<TConfig> extends ArtObjectConfig<TConfig> {

    public TriggerConfig() {
    }

    public TriggerConfig(TConfig with) {
        super(with);
    }

    @Description({
            "Delay of the trigger,",
            "Use the 'time' (e.g.: 1h20s) annotation to specify the delay this trigger has.",
            "Delay means the time to wait before executing any actions and informing others about the execution of this trigger."
    })
    private String delay;

    public long getDelay() {
        return TimeUtil.parseTimeAsTicks(delay);
    }
}
