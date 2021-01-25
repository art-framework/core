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

import io.artframework.annotations.ConfigOption;
import io.artframework.annotations.Resolve;
import io.artframework.conf.ConfigFieldInformation;
import io.artframework.conf.DefaultConfigMap;
import io.artframework.conf.KeyValuePair;
import io.artframework.util.ConfigUtil;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * The ConfigMap is an immutable class that holds information about
 * the mapping between fields in a (config) class to their given values
 * provided by a list of {@link KeyValuePair}s.
 * <p>The class that creates a config map must use the {@link ConfigOption} annotation
 * to provide the required information about its fields to this {@code ConfigMap}.
 * <p>The {@code ConfigMap} makes heavy use of the {@link ConfigUtil} class to parse
 * and map the provided fields and values. Use it if you just need partial mapping functions.
 * @see ConfigOption
 * @see ConfigUtil
 */
public interface ConfigMap {

    /**
     * Creates a new config map from the provided field to config field information mapping.
     * <p>Use the {@link ConfigUtil#getConfigFields(Class)} method to get the field information
     * or directly use the {@link #of(Class)} method in this class.
     *
     * @param configFields the config fields for the config map
     * @return the new default config map
     */
    static ConfigMap of(Map<String, ConfigFieldInformation> configFields) {
        return new DefaultConfigMap(configFields);
    }

    /**
     * Creates a new config map for the provided class extracting the fields.
     *
     * @param configClass the config class to create a config map for
     * @return the new default config map instance
     * @throws ConfigurationException if the config fields in the class are not configured correctly
     */
    static ConfigMap of(Class<?> configClass) throws ConfigurationException {

        return of(ConfigUtil.getConfigFields(configClass));
    }

    /**
     * @return true if the config map has been loaded {@link #with(List)} config values
     */
    boolean loaded();

    /**
     * @return the raw field name to config field information mapping
     */
    Map<String, ConfigFieldInformation> configFields();

    /**
     * Loads the config map with the given key value pairs mapping them to the correct
     * config fields.
     * <p>The map can be loaded multiple times but the last call will always override and erase all prior config values.
     *
     * @param keyValuePairs the list of key value pairs that should be loaded into this config map
     * @return a new config map with the loaded key value pairs. {@link #loaded()} is now true.
     * @throws ConfigurationException if the provided list of key value pairs cannot be mapped to this config,
     *                                e.g. if required fields are missing from the value list.
     */
    ConfigMap with(@NonNull List<KeyValuePair> keyValuePairs) throws ConfigurationException;

    /**
     * Resolves all values in this config map using the provided scope.
     * <p>All config fields that are tagged with @{@link Resolve} and not primitive values
     * will be resolved using the registered {@link Resolver}.
     * <p>Values in this config map can only resolve after they were provided {@link #with(List)}.
     *
     * @param scope the scope used when resolving the values of this config map. must not be null.
     * @param target the target of the resolution. can be null.
     * @param context the execution context that called the resolution. can be null.
     * @return a new config map instance with the resolved values
     */
    ConfigMap resolve(@NonNull Scope scope, @Nullable Target<?> target, @Nullable ExecutionContext<?> context);

    /**
     * Applies the loaded and mapped config values of this config map to the given config instance.
     * <p>Will inject the mapped fields with the values provided from {@link #with(List)} into the config.
     * <p>The config object is not altered if the config values have not been provided by calling {@code with(List)}.
     *
     * @param <TConfig> the type of the config
     * @param config the config that should have its config fields injected
     * @return the provided config instance
     */
    <TConfig> TConfig applyTo(@NonNull TConfig config);
}
