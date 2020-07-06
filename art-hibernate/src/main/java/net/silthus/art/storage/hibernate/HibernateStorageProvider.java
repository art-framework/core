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

package net.silthus.art.storage.hibernate;

import lombok.Getter;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.api.trigger.Target;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Getter
@Singleton
public class HibernateStorageProvider implements StorageProvider {

    public static final String STORAGE_TYPE = "hibernate";

    private final Connection connection;

    @Inject
    public HibernateStorageProvider(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <TValue> void store(Target<?> target, String key, TValue tValue) {
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
