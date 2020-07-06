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

import io.ebean.DB;
import net.silthus.art.api.trigger.AbstractTarget;
import net.silthus.art.storage.persistence.entities.MetadataKey;
import net.silthus.art.storage.persistence.entities.MetadataStore;
import net.silthus.art.storage.persistence.entities.query.QMetadataStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PersistenceStorage")
class PersistenceStorageProviderTest {

    private PersistenceStorageProvider storageProvider;

    @BeforeEach
    void beforeEach() {
        storageProvider = new PersistenceStorageProvider();
    }

    @AfterEach
    void afterEach() {
        DB.getDefault().deleteAllPermanent(MetadataStore.class, new QMetadataStore().findIds());
    }

    @Test
    @DisplayName("should store data")
    void shouldStoreData() {

        storageProvider.store(new StringTarget("foobar"), "foo", "test");

        assertThat(new QMetadataStore().findCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("should store different entry for different target")
    void shouldStoreMultipleEntriesForDifferentEntities() {

        storageProvider.store(new StringTarget("foo"), "key", "test");
        storageProvider.store(new StringTarget("bar"), "key", "test");

        assertThat(new QMetadataStore().findCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("should store multiple entries for the same target")
    void shouldStoreMultipleEntriesForTheSameTarget() {

        storageProvider.store(new StringTarget("foo"), "key1", 2);
        storageProvider.store(new StringTarget("foo"), "key2", true);
        storageProvider.store(new StringTarget("foo"), "key3", "foo");

        assertThat(new QMetadataStore().findCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("should update existing entry")
    void shouldUpdateExistingEntry() {

        storageProvider.store(new StringTarget("foo"), "key1", 2);
        assertThat(new QMetadataStore().metadataKey.equalTo(new MetadataKey("foo", "key1")).findOne())
                .extracting(MetadataStore::getMetadataValue)
                .isEqualTo("2");

        storageProvider.store(new StringTarget("foo"), "key1", "foo");
        assertThat(new QMetadataStore().metadataKey.equalTo(new MetadataKey("foo", "key1")).findOne())
                .extracting(MetadataStore::getMetadataValue)
                .isEqualTo("foo");
    }

    public static class StringTarget extends AbstractTarget<String> {

        protected StringTarget(String source) {
            super(source);
        }

        @Override
        public String getUniqueId() {
            return getSource();
        }
    }
}