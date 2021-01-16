package io.artframework.bukkit.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.artframework.ArtObjectContext;
import io.artframework.Scope;
import io.artframework.StorageProvider;
import io.artframework.Target;
import io.artframework.impl.DefaultMapStorageProvider;
import io.ebean.Database;
import io.ebean.meta.MetricData;
import io.ebeaninternal.server.lib.Str;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import javax.print.attribute.standard.MediaSize;
import java.util.*;
import java.util.function.Supplier;

public class EbeanPersistenceProvider extends DefaultMapStorageProvider implements StorageProvider {

    @Getter
    private final Database database;
    private final Map<String, Object> cache = new HashMap<>();
    private final Gson gson = new Gson();

    public EbeanPersistenceProvider(Scope scope, Database database) {
        super(scope);
        this.database = database;
    }

    public void reload() {

        cache.clear();
    }

    @Override
    public <TValue> Optional<TValue> set(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value) {

        final String json = gson.toJson(value);
        final Runnable runnable = () -> MetadataStore.find(target, key).ifPresentOrElse(
                metadataStore -> metadataStore.metaValue(json).update(),
                () -> new MetadataStore(key, json).target(target.uniqueId()).insert()
        );

        return updateCache(cacheKey(target, key), value, runnable);
    }

    @Override
    public <TValue> Optional<TValue> set(@NonNull ArtObjectContext<?> context, @NonNull Target<?> target, @NonNull String key, TValue value) {

        final String json = gson.toJson(value);
        final Runnable runnable = () -> MetadataStore.find(context, target, key).ifPresentOrElse(
                metadataStore -> metadataStore.metaValue(json).update(),
                () -> new MetadataStore(key, json)
                        .context(context.uniqueId())
                        .contextType(context.meta().artObjectClass().getCanonicalName())
                        .cacheKey(context.storageKey())
                        .target(target.uniqueId())
                        .insert()
        );

        return updateCache(cacheKey(context, target, key), value, runnable);
    }

    @Override
    public <TValue> Optional<TValue> set(@NonNull String key, @NonNull TValue value) {

        final String json = gson.toJson(value);
        final Runnable runnable = () -> MetadataStore.find(key).ifPresentOrElse(
                metadataStore -> metadataStore.metaValue(json).update(),
                () -> new MetadataStore(key, json).insert()
        );

        return updateCache(key, value, runnable);
    }

    @Override
    public <TValue> Optional<TValue> get(String key, Class<TValue> valueClass) {

        return getCache(key, valueClass, () -> MetadataStore.find(key));
    }

    @Override
    public <TValue> Optional<TValue> get(@NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass) {

        return getCache(cacheKey(target, key), valueClass, () -> MetadataStore.find(target, key));
    }

    @Override
    public <TValue> Optional<TValue> get(@NonNull ArtObjectContext<?> context, @NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass) {

        return getCache(cacheKey(context, target, key), valueClass, () -> MetadataStore.find(context, target, key));
    }

    @SuppressWarnings("unchecked")
    private <TValue> Optional<TValue> getCache(String key, Class<TValue> valueClass, Supplier<Optional<MetadataStore>> store) {

        return Optional.ofNullable((TValue) cache.computeIfAbsent(key,
                id -> store.get()
                        .map(MetadataStore::metaValue)
                        .map(value -> gson.fromJson(value, valueClass))
                        .orElse(null)));
    }

    @SuppressWarnings("unchecked")
    private <TValue> Optional<TValue> updateCache(String key, TValue value, Runnable runnable) {

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

    private String cacheKey(Target<?> target, String key) {

        return target.uniqueId() + key;
    }

    private String cacheKey(ArtObjectContext<?> context, Target<?> target, String key) {

        return context.uniqueId() + context.storageKey() + target.uniqueId() + key;
    }
}
