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

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.api.trigger.Target;
import net.silthus.art.storage.persistence.entities.MetadataKey;
import net.silthus.art.storage.persistence.entities.MetadataStore;
import net.silthus.art.storage.persistence.entities.query.QMetadataStore;

import java.util.Optional;

public class PersistenceStorageProvider implements StorageProvider {

    public static final String STORAGE_TYPE = "ebean";

    @Override
    public <TValue> void store(Target<?> target, String key, TValue tValue) {

        MetadataKey metadataKey = new MetadataKey(target.getUniqueId(), key);
        Optional<MetadataStore> entry = new QMetadataStore().metadataKey.eq(metadataKey).findOneOrEmpty();
        if (entry.isPresent()) {
            entry.get().setMetadataValue(tValue.toString())
                    .save();
        } else {
            new MetadataStore(metadataKey, tValue.toString()).save();
        }
    }

    @Override
    public <TValue> void store(ArtContext<?, ?, ?> context, Target<?> target, String key, TValue tValue) {

    }

    @Override
    public <TValue> Optional<TValue> get(Target<?> target, String key, Class<TValue> tValueClass) {
        return Optional.empty();
    }

    @Override
    public <TValue> Optional<TValue> get(ArtContext<?, ?, ?> context, Target<?> target, String key, Class<TValue> tValueClass) {
        return Optional.empty();
    }
}
