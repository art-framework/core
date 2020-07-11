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
import net.silthus.art.Storage;
import net.silthus.art.Target;
import net.silthus.art.api.ArtContext;
import net.silthus.art.storage.persistence.entities.MetadataKey;
import net.silthus.art.storage.persistence.entities.MetadataStore;

import java.util.Optional;

public class PersistenceStorage implements Storage {

    public static final String STORAGE_TYPE = "ebean";

    @Getter
    private final Database database;

    @Inject
    public PersistenceStorage(Database database) {
        this.database = database;
    }

    @Override
    public <TValue> void data(Target<?> target, String key, TValue tValue) {
        data(target.getUniqueId(), key, tValue);
    }

    public <TValue> void data(ArtContext<?, ?, ?> context, Target<?> target, String key, TValue tValue) {
        data(context.getUniqueId() + "#" + target.getUniqueId(), key, tValue);
    }

    private <TValue> void data(String uniqueId, String key, TValue value) {

        MetadataKey metadataKey = new MetadataKey(uniqueId, key);
        Gson gson = new Gson();
        String json = gson.toJson(value);

        MetadataStore entry = getDatabase().find(MetadataStore.class, metadataKey);
        if (entry != null) {
            entry.setMetadataValue(json);
            getDatabase().save(entry);
        } else {
            getDatabase().save(new MetadataStore(metadataKey, json));
        }

    }

    @Override
    public <TValue> Optional<TValue> get(Target<?> target, String key, Class<TValue> tValueClass) {
        return get(target.getUniqueId(), key, tValueClass);
    }

    @Override
    public <TValue> Optional<TValue> get(ArtContext<?, ?, ?> context, Target<?> target, String key, Class<TValue> tValueClass) {
        return get(context.getUniqueId() + "#" + target.getUniqueId(), key, tValueClass);
    }

    private <TValue> Optional<TValue> get(String uniqueId, String key, Class<TValue> tValueClass) {

        MetadataKey metadataKey = new MetadataKey(uniqueId, key);

        MetadataStore store = getDatabase().find(MetadataStore.class, metadataKey);
        if (store == null) return Optional.empty();

        try {
            Gson gson = new Gson();
            return Optional.ofNullable(gson.fromJson(store.getMetadataValue(), tValueClass));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
