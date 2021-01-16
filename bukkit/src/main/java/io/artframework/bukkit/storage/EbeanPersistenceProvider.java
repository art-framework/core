package io.artframework.bukkit.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.artframework.Scope;
import io.artframework.StorageProvider;
import io.artframework.impl.DefaultMapStorageProvider;
import io.ebean.Database;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EbeanPersistenceProvider extends DefaultMapStorageProvider implements StorageProvider {

    @Getter
    private final Database database;
    private final Set<String> initializedKeys = new HashSet<>();

    public EbeanPersistenceProvider(Scope scope, Database database) {
        super(scope);
        this.database = database;
    }

    @Override
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

        return super.set(key, value);
    }

    @Override
    public <TValue> Optional<TValue> get(String key, Class<TValue> valueClass) {

        if (initializedKeys.contains(key)) {
            return super.get(key, valueClass);
        } else {
            initializedKeys.add(key);

            MetadataStore store = getDatabase().find(MetadataStore.class, key);
            if (store == null){
                return Optional.empty();
            }

            try {
                Gson gson = new Gson();
                TValue value = gson.fromJson(store.getMetaValue(), valueClass);
                super.set(key, value);

                return Optional.ofNullable(value);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }
}
