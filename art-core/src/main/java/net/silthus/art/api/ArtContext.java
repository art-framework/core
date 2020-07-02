package net.silthus.art.api;

import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.api.trigger.Target;

import java.util.Objects;
import java.util.Optional;

public abstract class ArtContext<TTarget, TConfig, TContextOptions extends ArtObjectConfig<TConfig>> {

    @Getter(AccessLevel.PROTECTED)
    private final StorageProvider storageProvider;
    @Getter
    private final Class<TTarget> targetClass;
    private final TContextOptions config;

    public ArtContext(StorageProvider storageProvider, Class<TTarget> targetClass, TContextOptions config) {
        this.storageProvider = storageProvider;
        Objects.requireNonNull(targetClass, "targetClass must not be null");
        Objects.requireNonNull(config, "config must not be null");
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
    @SuppressWarnings("rawtypes")
    public boolean isTargetType(Object target) {
        if (target instanceof Target) {
            return getTargetClass().isInstance(((Target) target).getSource());
        }
        return getTargetClass().isInstance(target);
    }
}
