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

package net.silthus.art;

import lombok.NonNull;
import net.silthus.art.Configuration;
import net.silthus.art.Scope;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractScope implements Scope {

    private final Configuration configuration;
    private final Map<String, Object> data = new HashMap<>();

    protected AbstractScope(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public @NonNull Configuration configuration() {
        return configuration;
    }

    @Override
    public @NonNull Map<String, Object> data() {
        return data;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <TValue> Optional<TValue> data(@NonNull String key, @Nullable TValue value) {
        Object existingData;
        if (value == null) {
            existingData = data().remove(key);
        } else {
            existingData = data().put(key, value);
        }

        if (existingData != null && value != null) {
            try {
                return Optional.of((TValue) value.getClass().cast(existingData));
            } catch (ClassCastException ignored) {
            }
        }

        return Optional.empty();
    }

    @Override
    public <TValue> Optional<TValue> data(@NonNull String key, @NonNull Class<TValue> valueClass) {
        if (!data().containsKey(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(valueClass.cast(data().get(key)));
    }
}
