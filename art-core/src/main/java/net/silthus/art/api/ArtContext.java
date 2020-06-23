package net.silthus.art.api;

import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.art.api.config.ArtObjectConfig;

import java.util.Optional;

public abstract class ArtContext<TTarget, TConfig, TContextOptions extends ArtObjectConfig<TConfig>> {

    @Getter(AccessLevel.PACKAGE)
    private final Class<TTarget> targetClass;
    private final TContextOptions config;

    public ArtContext(Class<TTarget> targetClass, TContextOptions config) {
        this.targetClass = targetClass;
        this.config = config;
    }

    public TContextOptions getOptions() {
        return config;
    }

    /**
     * If present gets the config that was loaded for this {@link ArtContext}.
     * Use the config to make your {@link ArtObject} configurable.
     *
     * @return ARTObject specific config.
     */
    public Optional<TConfig> getConfig() {
        return config.getWith();
    }

    /**
     * Tests if the given object matches the required target type of the ARTObject.
     *
     * @param target target object to test
     * @return true if types match
     */
    public boolean isTargetType(Object target) {
        return getTargetClass().isInstance(target);
    }
}
