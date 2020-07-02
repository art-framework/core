package net.silthus.art.storage;

import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.api.trigger.Target;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class MemoryStorageProvider implements StorageProvider {

    @Inject
    @Getter(AccessLevel.PROTECTED)
    private Logger logger;

    private final Map<String, Map<String, Object>> storage = new HashMap<>();

    @Override
    public <TValue> void store(Target<?> target, String key, TValue tValue) {
        getStorage(target.getUniqueId()).put(key, tValue);
    }

    @Override
    public <TValue> void store(ArtContext<?, ?, ?> context, Target<?> target, String key, TValue tValue) {
        getStorage(getContextTargetId(context, target)).put(key, tValue);
    }

    @Override
    @Nullable
    public <TValue> TValue get(Target<?> target, String key, Class<TValue> valueClass) {
        return getValue(getStorage(target.getUniqueId()), key, valueClass);
    }

    @Override
    @Nullable
    public <TValue> TValue get(ArtContext<?, ?, ?> context, Target<?> target, String key, Class<TValue> valueClass) {
        return getValue(getStorage(getContextTargetId(context, target)), key, valueClass);
    }

    @Nullable
    private <TValue> TValue getValue(Map<String, Object> storage, String key, Class<TValue> valueClass) {
        try {
            return valueClass.cast(storage.get(key));
        } catch (ClassCastException e) {
            getLogger().warning("Failed to retrieve object '" + key + "' from storage: " + e.getMessage());
            return null;
        }
    }

    private String getContextTargetId(ArtContext<?, ?, ?> context, Target<?> target) {
        return context.getOptions().getIdentifier() + "#" + target.getUniqueId();
    }

    private Map<String, Object> getStorage(String identifier) {
        if (!storage.containsKey(identifier)) {
            storage.put(identifier, new HashMap<>());
        }
        return storage.get(identifier);
    }
}
