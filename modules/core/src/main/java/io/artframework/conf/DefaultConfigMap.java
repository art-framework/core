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

import com.google.common.collect.ImmutableMap;
import io.artframework.ConfigMap;
import io.artframework.ConfigurationException;
import io.artframework.parser.flow.ConfigMapType;
import io.artframework.util.ConfigUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
@Accessors(fluent = true)
public class DefaultConfigMap implements ConfigMap {

    @Getter
    ConfigMapType type;
    @Getter
    Map<String, ConfigFieldInformation> configFields;
    Map<ConfigFieldInformation, Object> configValues;
    boolean loaded;

    public DefaultConfigMap(ConfigMapType type, Map<String, ConfigFieldInformation> configFields) {
        this.type = type;
        this.configFields = ImmutableMap.copyOf(configFields);
        this.configValues = new HashMap<>();
        this.loaded = false;
    }

    DefaultConfigMap(ConfigMapType type, Map<String, ConfigFieldInformation> configFields, Map<ConfigFieldInformation, Object> configValues) {
        this.type = type;
        this.configFields = ImmutableMap.copyOf(configFields);
        this.configValues = ImmutableMap.copyOf(configValues);
        this.loaded = true;
    }

    @Override
    public <TConfig> TConfig applyTo(@NonNull TConfig config) {
        if (!this.loaded()) return config;
        setConfigFields(config, configValues);
        return config;
    }

    @Override
    public ConfigMap with(@NonNull List<KeyValuePair> keyValuePairs) throws ConfigurationException {
        return new DefaultConfigMap(type(), configFields(), ConfigUtil.loadConfigValues(configFields(), keyValuePairs));
    }

    private void setConfigFields(Object config, Map<ConfigFieldInformation, Object> fieldValueMap) {
        fieldValueMap.forEach((configFieldInformation, o) -> setConfigField(config, configFieldInformation, o));
    }

    private void setConfigField(Object config, ConfigFieldInformation fieldInformation, Object value) {

        try {
            if (fieldInformation.identifier().contains(".")) {
                // handle nested config objects
                String nestedIdentifier = StringUtils.substringBefore(fieldInformation.identifier(), ".");
                Field parentField = config.getClass().getDeclaredField(nestedIdentifier);
                parentField.setAccessible(true);
                Object nestedConfigObject = parentField.get(config);
                setConfigField(nestedConfigObject, fieldInformation.withIdentifier(nestedIdentifier), value);
            } else {
                Field field = config.getClass().getDeclaredField(fieldInformation.name());
                field.setAccessible(true);
                field.set(config, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
