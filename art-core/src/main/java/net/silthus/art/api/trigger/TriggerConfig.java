package net.silthus.art.api.trigger;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.config.ArtObjectConfig;

@Getter
@ConfigurationElement
@EqualsAndHashCode(callSuper = true)
public class TriggerConfig<TConfig> extends ArtObjectConfig<TConfig> {

    public TriggerConfig() {
    }

    public TriggerConfig(TConfig with) {
        super(with);
    }
}
