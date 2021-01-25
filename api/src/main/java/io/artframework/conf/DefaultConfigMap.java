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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.artframework.*;
import io.artframework.parser.ConfigParser;
import io.artframework.util.ReflectionUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Value
@Log(topic = "art-framework")
@Accessors(fluent = true)
public class DefaultConfigMap implements ConfigMap {

    @Getter
    Map<String, ConfigFieldInformation> configFields;
    List<ConfigValue> configValues;
    boolean loaded;

    public DefaultConfigMap(Map<String, ConfigFieldInformation> configFields) {
        this.configFields = ImmutableMap.copyOf(configFields);
        this.configValues = new ArrayList<>();
        this.loaded = false;
    }

    DefaultConfigMap(Map<String, ConfigFieldInformation> configFields, List<ConfigValue> configValues) {
        this.configFields = ImmutableMap.copyOf(configFields);
        this.configValues = ImmutableList.copyOf(configValues);
        this.loaded = true;
    }

    @Override
    public ConfigMap with(@NonNull List<KeyValuePair> keyValuePairs) throws ConfigurationException {
        return new DefaultConfigMap(configFields(), loadConfigValues(keyValuePairs));
    }

    @Override
    public ConfigMap resolve(@NonNull Scope scope, @Nullable Target<?> target, @Nullable ExecutionContext<?> context) {

        if (!loaded()) return this;

        ArrayList<ConfigValue> resolvedValues = new ArrayList<>();
        for (ConfigValue configValue : configValues()) {
            if (configValue.field().resolve() && configValue.value() instanceof String) {
                Optional<? extends ResolverFactory<?>> factory;
                if (configValue.field().resolvers() != null && configValue.field().resolvers().length > 0) {
                    factory = Arrays.stream(configValue.field().resolvers()).findFirst()
                            .flatMap(aClass -> scope.configuration().resolvers().get(configValue.field().type(), aClass));
                } else {
                    factory = scope.configuration().resolvers().get(configValue.field().type());
                }
                resolvedValues.add(configValue.withValue(factory.map(resolverFactory -> {
                    try {
                        ConfigParser parser = ConfigParser.of(resolverFactory.configMap());
                        if (parser.accept(configValue.value().toString())) {
                            List<KeyValuePair> configValues = parser.extractKeyValuePairs();
                            return resolverFactory.create(configValues).resolve(ResolverContext.of(scope, resolverFactory.configMap(), configValues, target, context));
                        }
                    } catch (ConfigurationException | ParseException | ResolveException e) {
                        log.severe("unable to resolve config \"" + configValue.value() + "\" for " + configValue.field() + ": " + e.getMessage());
                        e.printStackTrace();
                    }

                    return null;
                }).orElse(null)));
            } else {
                resolvedValues.add(configValue);
            }
        }

        List<ConfigFieldInformation> missingRequiredFields = resolvedValues.stream()
                .filter(value -> value.field().required())
                .filter(value -> value.value() == null)
                .map(ConfigValue::field)
                .collect(Collectors.toList());
        if (!missingRequiredFields.isEmpty()) {
            log.severe("Config is missing " + missingRequiredFields.size() + " required parameters: "
                    + missingRequiredFields.stream().map(ConfigFieldInformation::identifier).collect(Collectors.joining(",")));
        }

        return new DefaultConfigMap(configFields(), resolvedValues);
    }

    @Override
    public <TConfig> TConfig applyTo(@NonNull TConfig config) {
        if (!this.loaded()) return config;
        setConfigFields(config);
        return config;
    }

    private void setConfigFields(Object config) {
        configValues.forEach(value -> setConfigField(config, value));
    }

    private void setConfigField(Object config, ConfigValue value) {

        try {
            if (value.field().identifier().contains(".")) {
                // handle nested config objects
                String nestedIdentifier = StringUtils.substringBefore(value.field().identifier(), ".");
                Field parentField = ReflectionUtil.getDeclaredField(config.getClass(), nestedIdentifier)
                        .orElse(null);
                if (parentField == null) {
                    throw new NoSuchFieldException("No field with the name " + nestedIdentifier + " found in: " + config);
                }

                parentField.setAccessible(true);
                Object nestedConfigObject = parentField.get(config);
                if (nestedConfigObject == null) {
                    nestedConfigObject = parentField.getType().getConstructor().newInstance();
                    parentField.set(config, nestedConfigObject);
                }

                setConfigField(nestedConfigObject, value.withIdentifier(StringUtils.substringAfter(value.field().identifier(), ".")));
            } else {
                Field field = ReflectionUtil.getDeclaredField(config.getClass(), value.field().name())
                        .orElse(null);
                if (field == null) {
                    throw new NoSuchFieldException("No field with the name " + value.field().name() + " found in: " + config);
                }

                field.setAccessible(true);
                field.set(config, value.value());
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public List<ConfigValue> loadConfigValues(@NonNull List<KeyValuePair> keyValuePairs) throws ConfigurationException {

        if (configFields.isEmpty()) return new ArrayList<>();

        List<ConfigValue> fieldValues = new ArrayList<>();
        Set<ConfigFieldInformation> mappedFields = new HashSet<>();

        boolean usedKeyValue = false;

        for (int i = 0; i < keyValuePairs.size(); i++) {
            KeyValuePair keyValue = keyValuePairs.get(i);
            ConfigFieldInformation configFieldInformation = null;
            if (keyValue.getKey().isPresent() && configFields.containsKey(keyValue.getKey().get())) {
                configFieldInformation = configFields.get(keyValue.getKey().get());
                usedKeyValue = true;
            } else if (configFields.size() == 1 && keyValue.getKey().isEmpty()) {
                configFieldInformation = configFields.values().stream().findFirst().get();
            } else if (keyValue.getKey().isEmpty()) {
                if (usedKeyValue) {
                    throw new ConfigurationException("Positioned parameter found after key=value pair usage. Positioned parameters must come first.");
                }
                int finalI = i;
                Optional<ConfigFieldInformation> optionalFieldInformation = configFields.values().stream().filter(info -> info.position() == finalI).findFirst();
                if (optionalFieldInformation.isEmpty()) {
                    throw new ConfigurationException("Config does not define positioned parameters. Use key value pairs instead.");
                }
                configFieldInformation = optionalFieldInformation.get();
            }

            if (configFieldInformation == null) {
                log.warning("No matching field for key " + keyValue.getKey().orElse("n/a") + " found!");
                continue;
            }

            if (keyValue.getValue().isEmpty()) {
                throw new ConfigurationException("Config " + configFieldInformation.identifier() + " has an empty value.");
            }

            if (mappedFields.contains(configFieldInformation)) {
                log.warning("not mapping extraneous key value pair: " + keyValue);
                continue;
            }

            Object value = ReflectionUtil.toObject(configFieldInformation.type(), keyValue.getValue().get());

            fieldValues.add(new ConfigValue(configFieldInformation, value));
            mappedFields.add(configFieldInformation);
        }

        List<ConfigFieldInformation> missingRequiredFields = configFields.values().stream()
                .filter(ConfigFieldInformation::required)
                .filter(configFieldInformation -> !mappedFields.contains(configFieldInformation))
                .collect(Collectors.toList());

        if (!missingRequiredFields.isEmpty()) {
            throw new ConfigurationException("Config is missing " + missingRequiredFields.size() + " required parameters: "
                    + missingRequiredFields.stream().map(ConfigFieldInformation::identifier).collect(Collectors.joining(",")));
        }

        return fieldValues;
    }
}
