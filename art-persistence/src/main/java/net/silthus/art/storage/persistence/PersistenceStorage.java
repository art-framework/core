/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.silthus.art.storage.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import io.ebean.Database;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.AbstractScope;
import net.silthus.art.Configuration;
import net.silthus.art.Storage;
import net.silthus.art.storage.persistence.entities.MetadataStore;

import java.util.Optional;

public class PersistenceStorage extends AbstractScope implements Storage {

    public static final String STORAGE_TYPE = "ebean";

    @Getter
    private final Database database;

    @Inject
    public PersistenceStorage(Configuration configuration, Database database) {
        super(configuration);
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
