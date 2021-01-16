package io.artframework.bukkit.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.artframework.Scope;
import io.artframework.StorageProvider;
import io.artframework.impl.DefaultMapStorageProvider;
import io.ebean.Database;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import java.util.*;

public class EbeanPersistenceProvider extends DefaultMapStorageProvider implements StorageProvider {

    @Getter
    private final Database database;
    private final Map<String, Object> cache = new HashMap<>();
    private final Gson gson = new Gson();

    public EbeanPersistenceProvider(Scope scope, Database database) {
        super(scope);
        this.database = database;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TValue> Optional<TValue> set(@NonNull String key, @NonNull TValue value) {

        final Runnable runnable = () -> {
            Gson gson = new Gson();
            String json = gson.toJson(value);

            MetadataStore entry = getDatabase().find(MetadataStore.class, key);
            if (entry != null) {
                entry.setMetaValue(json);
                getDatabase().save(entry);
            } else {
                getDatabase().save(new MetadataStore(key, json));
            }
        };

        scope().configuration().scheduler().ifPresentOrElse(
                scheduler -> scheduler.runTaskAsynchronously(runnable),
                runnable
        );

        Object existingValue = cache.put(key, value);

        if (value.getClass().isInstance(existingValue)) {
            return Optional.of((TValue) existingValue);
        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TValue> Optional<TValue> get(String key, Class<TValue> valueClass) {

        return Optional.ofNullable((TValue) cache.computeIfAbsent(key, s -> {
            MetadataStore store = getDatabase().find(MetadataStore.class, key);
            if (store == null) {
                return null;
            }

            try {
                return gson.fromJson(store.getMetaValue(), valueClass);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }));
    }
}
