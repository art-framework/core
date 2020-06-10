package net.silthus.art.api.trigger;

import lombok.EqualsAndHashCode;
import net.silthus.art.api.ARTContext;

import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
public class TriggerContext<TTarget, TConfig> extends ARTContext<TTarget> {

    private final TriggerConfig<TConfig> config;

    public TriggerContext(Class<TTarget> targetClass, TriggerConfig<TConfig> config) {
        super(targetClass);
        this.config = config;
    }

    public Optional<TConfig> getConfig() {
        return config.getWith();
    }
}
