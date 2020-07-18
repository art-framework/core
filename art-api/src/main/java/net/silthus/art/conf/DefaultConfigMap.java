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

package net.silthus.art.conf;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.ArtConfigException;
import net.silthus.art.ConfigMap;
import net.silthus.art.parser.flow.ConfigMapType;
import net.silthus.art.util.ReflectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public final class DefaultConfigMap implements ConfigMap {

    @Getter
    private final ConfigMapType type;
    @Getter
    private final Map<String, ConfigFieldInformation> configFields;
    private final Map<ConfigFieldInformation, Object> configValues = new HashMap<>();
    @Getter
    private boolean loaded;

    public DefaultConfigMap(ConfigMapType type, Map<String, ConfigFieldInformation> configFields) {
        this.type = type;
        this.configFields = ImmutableMap.copyOf(configFields);
    }

    @Override
    public <TConfig> TConfig applyTo(@NonNull TConfig config) {
        if (!isLoaded()) return config;
        setConfigFields(config, configValues);
        return config;
    }

    @Override
    public ConfigMap loadValues(@NonNull List<KeyValuePair> keyValuePairs) throws ArtConfigException {

        Map<ConfigFieldInformation, Object> fieldValueMap = new HashMap<>();
        Set<ConfigFieldInformation> mappedFields = new HashSet<>();

        boolean usedKeyValue = false;

        for (int i = 0; i < keyValuePairs.size(); i++) {
            KeyValuePair keyValue = keyValuePairs.get(i);
            ConfigFieldInformation configFieldInformation;
            if (keyValue.getKey().isPresent() && getConfigFields().containsKey(keyValue.getKey().get())) {
                configFieldInformation = getConfigFields().get(keyValue.getKey().get());
                usedKeyValue = true;
            } else if (getConfigFields().size() == 1) {
                //noinspection OptionalGetWithoutIsPresent
                configFieldInformation = getConfigFields().values().stream().findFirst().get();
            } else {
                if (usedKeyValue) {
                    throw new ArtConfigException("Positioned parameter found after key=value pair usage. Positioned parameters must come first.");
                }
                int finalI = i;
                Optional<ConfigFieldInformation> optionalFieldInformation = getConfigFields().values().stream().filter(info -> info.getPosition() == finalI).findFirst();
                if (!optionalFieldInformation.isPresent()) {
                    throw new ArtConfigException("Config does not define positioned parameters. Use key value pairs instead.");
                }
                configFieldInformation = optionalFieldInformation.get();
            }

            if (!keyValue.getValue().isPresent()) {
                throw new ArtConfigException("Config " + configFieldInformation.getIdentifier() + " has an empty value.");
            }

            Object value = ReflectionUtil.toObject(configFieldInformation.getType(), keyValue.getValue().get());

            fieldValueMap.put(configFieldInformation, value);
            mappedFields.add(configFieldInformation);
        }

        List<ConfigFieldInformation> missingRequiredFields = getConfigFields().values().stream()
                .filter(ConfigFieldInformation::isRequired)
                .filter(configFieldInformation -> !mappedFields.contains(configFieldInformation))
                .collect(Collectors.toList());

        if (!missingRequiredFields.isEmpty()) {
            throw new ArtConfigException("Config is missing " + missingRequiredFields.size() + " required parameters: "
                    + missingRequiredFields.stream().map(ConfigFieldInformation::getIdentifier).collect(Collectors.joining(",")));
        }

        configValues.clear();
        configValues.putAll(fieldValueMap);
        loaded = true;

        return this;
    }

    private void setConfigFields(Object config, Map<ConfigFieldInformation, Object> fieldValueMap) {
        fieldValueMap.forEach((configFieldInformation, o) -> setConfigField(config, configFieldInformation, o));
    }

    private void setConfigField(Object config, ConfigFieldInformation fieldInformation, Object value) {

        try {
            if (fieldInformation.getIdentifier().contains(".")) {
                // handle nested config objects
                String nestedIdentifier = StringUtils.substringBefore(fieldInformation.getIdentifier(), ".");
                Field parentField = config.getClass().getDeclaredField(nestedIdentifier);
                parentField.setAccessible(true);
                Object nestedConfigObject = parentField.get(config);
                setConfigField(nestedConfigObject, fieldInformation.copyOf(nestedIdentifier), value);
            } else {
                Field field = config.getClass().getDeclaredField(fieldInformation.getName());
                field.setAccessible(true);
                field.set(config, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
