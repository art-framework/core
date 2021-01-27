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

package io.artframework.util;

import com.google.common.base.Strings;
import io.artframework.BootstrapModule;
import io.artframework.ConfigurationException;
import io.artframework.FieldNameFormatter;
import io.artframework.Resolver;
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;
import io.artframework.annotations.Config;
import io.artframework.annotations.ConfigOption;
import io.artframework.annotations.Ignore;
import io.artframework.annotations.Resolve;
import io.artframework.conf.ConfigFieldInformation;
import io.artframework.conf.FieldNameFormatters;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reflections.ReflectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log(topic = "art-framework")
public final class ConfigUtil {

    public static Map<String, ConfigFieldInformation> getConfigFields(Class<?> configClass, FieldNameFormatter formatter) throws ConfigurationException {
        try {
            Constructor<?> constructor = configClass.getConstructor();
            constructor.setAccessible(true);
            return getConfigFields("", configClass, constructor.newInstance(), formatter);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ConfigurationException("Unable to create instance of config class \"" + configClass.getSimpleName() + "\": " + e.getMessage()
                    + ". Is it public and has a public no args constructor?", e);
        }
    }

    public static Map<String, ConfigFieldInformation> getConfigFields(Class<?> configClass) throws ConfigurationException {
        return getConfigFields(configClass, FieldNameFormatters.LOWER_UNDERSCORE);
    }

    public static <TConfig> Map<String, ConfigFieldInformation> getConfigFields(Class<TConfig> configClass, TConfig config) throws ConfigurationException {
        return getConfigFields(configClass, config, FieldNameFormatters.LOWER_UNDERSCORE);
    }

    public static <TConfig> Map<String, ConfigFieldInformation> getConfigFields(Class<TConfig> configClass, TConfig config, FieldNameFormatter formatter) throws ConfigurationException {
        return getConfigFields("", configClass, config, formatter);
    }

    private static Map<String, ConfigFieldInformation> getConfigFields(String basePath, Class<?> configClass, Object configInstance, FieldNameFormatter formatter) throws ConfigurationException {
        Map<String, ConfigFieldInformation> fields = new HashMap<>();

        try {
            Field[] configFields;
            if (configClass.isAnnotationPresent(ConfigOption.class)) {
                configFields = FieldUtils.getAllFields(configClass);
            } else {
                configFields = ArrayUtils.addAll(
                        FieldUtils.getFieldsWithAnnotation(configClass, ConfigOption.class),
                        FieldUtils.getFieldsWithAnnotation(configClass, Resolve.class)
                );
            }

            for (Field field : configFields) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (field.isAnnotationPresent(Ignore.class)) continue;
                if (Modifier.isFinal(field.getModifiers())) {
                    if (field.isAnnotationPresent(ConfigOption.class)) {
                        throw new ConfigurationException("Cannot use a final field as a config option. Remove the @ConfigOption or the final modifier from \"" + field.getName() + "\"");
                    }
                    continue;
                }

                Optional<ConfigOption> configOption = getAnnotation(field, ConfigOption.class);
                boolean resolve = getAnnotation(field, Resolve.class).isPresent();

                String identifier = basePath + configOption.map(ConfigOption::value)
                        .filter(s -> !Strings.isNullOrEmpty(s))
                        .orElse(formatter.apply(field.getName()));

                if (resolve || field.getType().isPrimitive() || field.getType().equals(String.class) || field.getType().isArray()) {

                    String[] description = configOption.map(ConfigOption::description).orElse(new String[0]);
                    Boolean required = configOption.map(ConfigOption::required).orElse(false);
                    Integer position = configOption.map(ConfigOption::position).orElse(-1);
                    Class<? extends Resolver<?>>[] resolvers = getAnnotation(field, Resolve.class)
                            .map(Resolve::value)
                            .orElse(null);

                    field.setAccessible(true);

                    Object defaultValue = field.get(configInstance);

                    if (field.getType().isArray() && defaultValue == null) {
                        defaultValue = Array.newInstance(field.getType().getComponentType(), 0);
                    }

                    fields.put(identifier, new ConfigFieldInformation(
                            identifier,
                            field.getName(),
                            field.getType(),
                            position,
                            description,
                            required,
                            defaultValue,
                            resolve,
                            resolvers
                    ));
                } else {
                    fields.putAll(getConfigFields(identifier + ".", field.getType(), field.getType().getConstructor().newInstance(), formatter));
                }
            }

            List<ConfigFieldInformation> sameFieldPosition = fields.values().stream().filter(field1 -> fields.values().stream().anyMatch(
                    field2 -> field1 != field2
                            && field1.position() > -1
                            && field2.position() > -1
                            && field1.position() == field2.position()
            )).collect(Collectors.toList());

            if (!sameFieldPosition.isEmpty()) {
                throw new ConfigurationException("found same position " + sameFieldPosition.get(0).position() + " on the following fields: "
                        + sameFieldPosition.stream().map(ConfigFieldInformation::identifier).collect(Collectors.joining(",")));
            }

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            throw new ConfigurationException(e);
        }

        return fields;
    }

    public static <TAnnotation extends Annotation> Optional<TAnnotation> getAnnotation(Field field, Class<TAnnotation> annotationClass) {

        if (field.isAnnotationPresent(annotationClass)) {
            return Optional.of(field.getAnnotation(annotationClass));
        }
        return Optional.empty();
    }

    /**
     * Tries to find the config file containing the given id.
     *
     * @param id id of the ART config
     * @return null if no config file containing the id was found.
     *          the absolute path to the config file if found.
     */
    public static Optional<String> getFileName(String id) {

        try {
            return Files.walk(new File("").toPath())
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> containsString(file, id))
                    .map(File::getAbsolutePath)
                    .findFirst();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static boolean containsString(File file, String string) {
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains(string)) {
                    return true;
                }
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <TObject> TObject injectConfigFields(@NonNull Scope scope, @NonNull TObject object) {

        File basePath;
        if (BootstrapModule.class.isAssignableFrom(object.getClass())) {
            basePath = scope.settings().basePath();
        } else if (object.getClass().isAnnotationPresent(ArtModule.class)) {
            basePath = scope.settings().modulePath(object.getClass().getAnnotation(ArtModule.class).value());
        } else {
            basePath = scope.settings().configPath();
        }

        basePath.mkdirs();

        Set<Field> configFields = ReflectionUtils.getAllFields(object.getClass(), field ->
                !Modifier.isStatic(field.getModifiers())
                        && !Modifier.isFinal(field.getModifiers())
                        && field.isAnnotationPresent(Config.class)

        );

        for (Field configField : configFields) {
            String configName = configField.getAnnotation(Config.class).value();
            File configFile = new File(basePath, configName);
            Optional<?> config = scope.configuration().configs().load(configField.getType(), configFile);
            if (config.isPresent()) {
                try {
                    configField.setAccessible(true);
                    configField.set(object, config.get());
                    log.info("injected " + configName + " config into field " + configField.getName() + " of " + object.getClass().getCanonicalName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return object;
    }

    public static String toConfigString(Map<String, ConfigFieldInformation> configMap) {

        final Function<Object, @Nullable String> convertToString = (input) -> {
            if (input == null) return "";
            if (input.getClass().isArray()) {
                return input.getClass().getComponentType().getSimpleName() + "...";
            } else {
                return input.toString();
            }
        };

        return configMap.values().stream().sorted()
                .map(info -> info.identifier()
                        + (info.required() ? "*" : "")
                        + "="
                        + convertToString.apply(info.defaultValue()))
                .collect(Collectors.joining(", "));
    }
}
