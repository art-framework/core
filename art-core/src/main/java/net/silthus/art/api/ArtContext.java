package net.silthus.art.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.config.ArtObjectConfig;

import java.util.Optional;

@EqualsAndHashCode(of = {"targetClass"})
public abstract class ArtContext<TTarget, TConfig> {

    @Getter
    private final Class<TTarget> targetClass;
    private final ArtObjectConfig<TConfig> config;

    public ArtContext(Class<TTarget> targetClass, ArtObjectConfig<TConfig> config) {
        this.targetClass = targetClass;
        this.config = config;
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
