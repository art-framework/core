package net.silthus.art.api;

import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.storage.StorageConstants;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.api.target.Target;

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

    /**
     * Stores a persistent value for the given target in the store using the {@link StorageProvider}.
     * Use the {@link #get(Target, String, Class)} method to retrieve your stored data.
     *
     * @param target target to store data for
     * @param key storage key
     * @param value value to store
     * @param <TValue> type of the value
     * @see StorageProvider#store(ArtContext, Target, String, Object)
     */
    public <TValue> void store(Target<?> target, String key, TValue value) {
        key = StorageConstants.LOCAL_KEY_PREFIX + key;
        getStorageProvider().store(this, target, key, value);
    }

    /**
     * Retrieves data stored with {@link #store(Target, String, Object)} from the {@link StorageProvider}.
     *
     * @param target target to get data for
     * @param key storage key
     * @param valueClass class of the value that was stored
     * @param <TValue> type of the value
     * @return stored value if the type matches and it exists. an empty optional otherwise.
     */
    public <TValue> Optional<TValue> get(Target<TTarget> target, String key, Class<TValue> valueClass) {
        key = StorageConstants.LOCAL_KEY_PREFIX + key;
        return getStorageProvider().get(this, target, key, valueClass);
    }
}
