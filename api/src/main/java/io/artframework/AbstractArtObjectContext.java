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

package io.artframework;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Accessors(fluent = true)
public abstract class AbstractArtObjectContext<TArtObject extends ArtObject> extends AbstractScoped implements ArtObjectContext<TArtObject> {

    private final ArtObjectMeta<TArtObject> information;
    @Getter
    private final Map<String, Object> data = new HashMap<>();
    @Getter
    @Setter
    private String storageKey = UUID.randomUUID().toString();

    public AbstractArtObjectContext(@NonNull Scope scope, ArtObjectMeta<TArtObject> information) {
        super(scope);
        this.information = information;
    }

    @Override
    public ArtObjectMeta<TArtObject> meta() {
        return information;
    }

    @Override
    public <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value) {
        return configuration().storage().set(this, target, key, value);
    }

    @Override
    public <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass) {
        return configuration().storage().get(this, target, key, valueClass);
    }
}
