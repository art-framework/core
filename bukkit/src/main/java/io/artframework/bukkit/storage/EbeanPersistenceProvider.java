package io.artframework.bukkit.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.artframework.*;
import io.artframework.impl.DefaultMapStorageProvider;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.Transaction;
import io.ebean.meta.MetricData;
import io.ebeaninternal.server.lib.Str;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;

import javax.print.attribute.standard.MediaSize;
import java.util.*;
import java.util.function.Supplier;

@Log(topic = "art-framework:ebean")
public class EbeanPersistenceProvider extends DefaultMapStorageProvider implements StorageProvider {

    @Getter
    private final Database database;
    private final Map<String, Object> cache = new HashMap<>();
    private final Map<UUID, MetadataStore> queuedTransactions = new HashMap<>();
    private final Gson gson = new Gson();
    private final Runnable saveRunnable;

    private Task task;

    public EbeanPersistenceProvider(Scope scope, Database database) {
        super(scope);
        this.database = database;
        saveRunnable = () -> {
            final List<MetadataStore> transactions = List.copyOf(queuedTransactions.values());
            queuedTransactions.clear();

            try (Transaction transaction = database.beginTransaction()) {
                for (MetadataStore store : transactions) {
                    if (store.id() == null) {
                        store.insert();
                    } else {
                        MetadataStore existing = MetadataStore.find.byId(store.id());
                        if (existing != null) {
                            existing.metaValue(store.metaValue()).save();
                        }
                    }
                }
                transaction.commit();
            }
        };
    }

    public void load() {

        task = startTask();
    }

    public void reload() {

        if (task != null) {
            task.cancel();
            saveRunnable.run();
        }

        cache.clear();
        task = startTask();
    }

    private Task startTask() {
        return scope().configuration().scheduler()
                .map(scheduler -> scheduler.runTaskTimerAsynchronously(saveRunnable, 1000L, 1000L)) // in milliseconds
                .orElse(null);
    }

    @Override
    public <TValue> Optional<TValue> set(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value) {

        final String json = gson.toJson(value);
        MetadataStore store = MetadataStore.find(target, key)
                .orElse(new MetadataStore(key, json))
                .metaValue(json);

        return updateCache(cacheKey(target, key), value, store);
    }

    @Override
    public <TValue> Optional<TValue> set(@NonNull ArtObjectContext<?> context, @NonNull Target<?> target, @NonNull String key, TValue value) {

        final String json = gson.toJson(value);
        MetadataStore store = MetadataStore.find(context, target, key)
                .orElse(new MetadataStore(key, json)
                        .context(context.uniqueId())
                        .contextType(context.meta().artObjectClass().getCanonicalName())
                        .cacheKey(context.storageKey())
                        .target(target.uniqueId()))
                .metaValue(json);

        return updateCache(cacheKey(context, target, key), value, store);
    }

    @Override
    public <TValue> Optional<TValue> set(@NonNull String key, @NonNull TValue value) {

        final String json = gson.toJson(value);
        MetadataStore store = MetadataStore.find(key)
                .orElse(new MetadataStore(key, json))
                .metaValue(json);

        return updateCache(key, value, store);
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
    private <TValue> Optional<TValue> updateCache(String key, TValue value, MetadataStore store) {

        Object existingValue = cache.put(key, value);
        queuedTransactions.put(store.id(), store);

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
