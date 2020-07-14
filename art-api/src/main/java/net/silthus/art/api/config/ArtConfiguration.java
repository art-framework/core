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

package net.silthus.art.api.config;

import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.configs.yaml.YamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.impl.DefaultMapStorage;

import java.nio.file.Path;

/**
 * The global configuration of ART.
 * This is provided by the implementing plugin.
 */
@Getter
@Setter
@ConfigurationElement
public class ArtConfiguration extends YamlConfiguration {

    private String storageProvider = DefaultMapStorage.STORAGE_TYPE;
    private DatabaseConfig database = new DatabaseConfig();

    public ArtConfiguration(Path path) {
        super(path, YamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }
}
