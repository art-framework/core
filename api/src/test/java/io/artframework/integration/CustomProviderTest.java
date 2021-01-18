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

package io.artframework.integration;

import io.artframework.Scope;
import io.artframework.StorageProvider;
import lombok.NonNull;

import java.util.Optional;

public class CustomProviderTest implements StorageProvider {

    private final Scope scope;

    public CustomProviderTest(Scope scope) {
        this.scope = scope;
    }

    @Override
    public <TValue> Optional<TValue> set(@NonNull String key, @NonNull TValue tValue) {
        return Optional.empty();
    }

    @Override
    public <TValue> Optional<TValue> get(String key, Class<TValue> tValueClass) {
        return Optional.empty();
    }

    @Override
    public Scope scope() {
        return scope;
    }

    @Override
    public void close() throws Exception {

    }
}
