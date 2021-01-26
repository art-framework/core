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

package io.artframework.conf;

import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * The KeyValuePair maps an optional key to an optional string value.
 * <p>It is primarily used in the {@link io.artframework.parser.ConfigParser}
 * to map the parsed values into the {@link io.artframework.ConfigMap}.
 */
@Value
@Accessors(fluent = true)
public class KeyValuePair {

    /**
     * Creates a new key value pair from the given key and value.
     * <p>Do not use empty keys. Provide a null key instead.
     *
     * @param key the key. can be null.
     * @param value the value. can be null.
     * @return the key value pair
     */
    public static KeyValuePair of(@Nullable String key, @Nullable String value) {
        return new KeyValuePair(key, value);
    }

    String key;
    @With
    String value;

    private KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Optional<String> key() {
        return Optional.ofNullable(key);
    }

    public Optional<String> value() {
        return Optional.ofNullable(value);
    }
}
