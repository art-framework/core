package net.silthus.art.api.actions;

import lombok.Data;
import org.apache.commons.lang.NotImplementedException;

@Data
public class ActionConfig<TConfig> {

    private String delay = "0s";
    private String cooldown = "0s";
    private boolean executeOnce = false;

    private TConfig with;

    public long getDelay() {
        throw new NotImplementedException();
    }

    public long getCooldown() {
        throw new NotImplementedException();
    }
}
