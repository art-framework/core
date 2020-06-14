package net.silthus.art.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.config.ARTObjectConfig;

@EqualsAndHashCode(of = {"targetClass"})
public abstract class ARTContext<TTarget, TConfig> {

    @Getter
    private final Class<TTarget> targetClass;
    private final ARTObjectConfig<TConfig> config;

    public ARTContext(Class<TTarget> targetClass, ARTObjectConfig<TConfig> config) {
        this.targetClass = targetClass;
        this.config = config;
    }

    /**
     * If present gets the config that was loaded for this {@link ARTContext}.
     * Use the config to make your {@link ARTObject} configurable.
     *
     * @return ARTObject specific config.
     */
    public TConfig getConfig() {
        return config.getWith();
    }
}
