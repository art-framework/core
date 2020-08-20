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

import io.artframework.impl.JacksonConfigProvider;

import java.io.File;
import java.util.Optional;

/**
 * Provides a way to load configs from the filesystem into objects.
 */
public interface ConfigProvider extends Scoped {

    static ConfigProvider of(Scope scope) {
        return new JacksonConfigProvider(scope);
    }

    /**
     * Loads the given config file using the art-framework base path.
     * <p>Will try to map the loaded config to the given config class.
     * Uses the config class as default values if no config is found.
     *
     * @param configClass the class of the config
     * @param file the name of the config file to load, e.g. config.yml
     * @param <TConfig> type of the config
     * @return the loaded config file or the created default config if it did not exist
     */
    default <TConfig> Optional<TConfig> load(Class<TConfig> configClass, String file) {
        return load(configClass, new File(scope().settings().basePath(), file));
    }

    /**
     * Loads the given config file using provided file.
     * <p>Will try to map the loaded config to the given config class.
     * Uses the config class as default values if no config is found.
     *
     * @param configClass the class of the config
     * @param file the file to load
     * @param <TConfig> type of the config
     * @return the loaded config file or the created default config if it did not exist
     */
    <TConfig> Optional<TConfig> load(Class<TConfig> configClass, File file);

    <TConfig> void save(TConfig config, File file);
}
