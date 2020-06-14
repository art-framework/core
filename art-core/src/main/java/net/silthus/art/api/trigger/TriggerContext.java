package net.silthus.art.api.trigger;

import lombok.EqualsAndHashCode;
import net.silthus.art.api.ARTContext;

@EqualsAndHashCode(callSuper = true)
public class TriggerContext<TTarget, TConfig> extends ARTContext<TTarget, TConfig> {

    public TriggerContext(Class<TTarget> targetClass, TriggerConfig<TConfig> config) {
        super(targetClass, config);
    }
}
