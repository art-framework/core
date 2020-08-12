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

package io.artframework.impl;

import io.artframework.AbstractScope;
import io.artframework.Configuration;
import io.artframework.StorageProvider;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultMapStorageProvider extends AbstractScope implements StorageProvider {

    private final Map<String, Object> storage = new HashMap<>();

    public DefaultMapStorageProvider(Configuration configuration) {
        super(configuration);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TValue> Optional<TValue> set(@NonNull String key, @NonNull TValue value) {
        Object existingValue = storage.put(key, value);
        if (value.getClass().isInstance(existingValue)) {
            return Optional.of((TValue) existingValue);
        }
        return Optional.empty();
    }

    @Override
    public <TValue> Optional<TValue> get(String key, Class<TValue> valueClass) {
        try {
            Object value = storage.get(key);
            return Optional.ofNullable(valueClass.cast(value));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
