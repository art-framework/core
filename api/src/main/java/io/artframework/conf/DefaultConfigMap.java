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
import io.artframework.Scope;
import io.artframework.util.ConfigUtil;
import io.artframework.util.ReflectionUtil;
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
    Map<String, ConfigFieldInformation> configFields;
    Map<ConfigFieldInformation, Object> configValues;
    boolean loaded;

    public DefaultConfigMap(Map<String, ConfigFieldInformation> configFields) {
        this.configFields = ImmutableMap.copyOf(configFields);
        this.configValues = new HashMap<>();
        this.loaded = false;
    }

    DefaultConfigMap(Map<String, ConfigFieldInformation> configFields, Map<ConfigFieldInformation, Object> configValues) {
        this.configFields = ImmutableMap.copyOf(configFields);
        this.configValues = ImmutableMap.copyOf(configValues);
        this.loaded = true;
    }

    @Override
    public <TConfig> TConfig applyTo(Scope scope, @NonNull TConfig config) {
        if (!this.loaded()) return config;
        setConfigFields(scope, config, configValues);
        return config;
    }

    @Override
    public ConfigMap with(@NonNull List<KeyValuePair> keyValuePairs) throws ConfigurationException {
        return new DefaultConfigMap(configFields(), ConfigUtil.loadConfigValues(configFields(), keyValuePairs));
    }

    private void setConfigFields(Scope scope, Object config, Map<ConfigFieldInformation, Object> configValues) {
        configValues.forEach((configFieldInformation, o) -> setConfigField(scope, config, configFieldInformation, o));
    }

    private void setConfigField(Scope scope, Object config, ConfigFieldInformation fieldInformation, Object value) {

        try {
            if (fieldInformation.identifier().contains(".")) {
                // handle nested config objects
                String nestedIdentifier = StringUtils.substringBefore(fieldInformation.identifier(), ".");
                Field parentField = ReflectionUtil.getDeclaredField(config.getClass(), nestedIdentifier)
                        .orElse(null);
                if (parentField == null) {
                    throw new NoSuchFieldException("No field with the name " + nestedIdentifier + " found in: " + config);
                }
                parentField.setAccessible(true);
                Object nestedConfigObject = parentField.get(config);
                setConfigField(scope, nestedConfigObject, fieldInformation.withIdentifier(nestedIdentifier), value);
            } else {
                Field field = ReflectionUtil.getDeclaredField(config.getClass(), fieldInformation.name())
                        .orElse(null);
                if (field == null) {
                    throw new NoSuchFieldException("No field with the name " + fieldInformation.name() + " found in: " + config);
                }
                setField(scope, config, field, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setField(@NonNull Scope scope, @NonNull Object config, @NonNull Field field, Object value) throws IllegalAccessException {

        if (!field.getType().isInstance(value) && value != null) {
            // TODO: resolve config value - maybe put the resolver information in the ConfigFieldInformation class
//            scope.configuration().resolvers().get(value.getClass())
//                    .map(resolverFactory -> resolverFactory.create(configValues))
//                    .map(resolver -> resolver.resolve())
        }

        field.setAccessible(true);
        field.set(config, value);
    }
}
