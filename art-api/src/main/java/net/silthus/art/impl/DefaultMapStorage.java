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

package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.Storage;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class DefaultMapStorage implements Storage {

    private final Map<String, Object> storage = new HashMap<>();

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
