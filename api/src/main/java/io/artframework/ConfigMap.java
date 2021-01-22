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

import io.artframework.conf.ConfigFieldInformation;
import io.artframework.conf.DefaultConfigMap;
import io.artframework.conf.KeyValuePair;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public interface ConfigMap {

    static ConfigMap of(Map<String, ConfigFieldInformation> configFields) {
        return new DefaultConfigMap(configFields);
    }

    <TConfig> TConfig applyTo(@NonNull TConfig config);

    Map<String, ConfigFieldInformation> configFields();

    boolean loaded();

    ConfigMap with(@NonNull List<KeyValuePair> keyValuePairs) throws ConfigurationException;
}
