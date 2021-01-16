package io.artframework.bukkit.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.artframework.Scope;
import io.artframework.StorageProvider;
import io.artframework.impl.DefaultMapStorageProvider;
import io.ebean.Database;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

public class EbeanPersistenceProvider extends DefaultMapStorageProvider implements StorageProvider {

    @Getter
    private final Database database;

    public EbeanPersistenceProvider(Scope scope, Database database) {
        super(scope);
        this.database = database;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TValue> Optional<TValue> set(@NonNull String key, @NonNull TValue value) {

        Gson gson = new Gson();
        String json = gson.toJson(value);

        MetadataStore entry = getDatabase().find(MetadataStore.class, key);
        if (entry != null) {
            TValue existingValue = (TValue) gson.fromJson(entry.getValue(), value.getClass());
            entry.setValue(json);
            getDatabase().save(entry);
            return Optional.ofNullable(existingValue);
        } else {
            getDatabase().save(new MetadataStore(key, json));
        }

        return Optional.empty();
    }

    @Override
    public <TValue> Optional<TValue> get(String key, Class<TValue> valueClass) {

        MetadataStore store = getDatabase().find(MetadataStore.class, key);
        if (store == null) return Optional.empty();

        try {
            Gson gson = new Gson();
            return Optional.ofNullable(gson.fromJson(store.getValue(), valueClass));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
