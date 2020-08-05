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

import io.artframework.ArtObject;
import io.artframework.ArtProvider;
import io.artframework.annotations.Config;
import io.artframework.annotations.ConfigOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

import java.io.Serializable;

/**
 * Settings that influence how ART works.
 */
@Data
@With
@ConfigOption
@AllArgsConstructor
@Config("settings.yml")
@EqualsAndHashCode(callSuper = false)
public class Settings extends SettingsBase implements Serializable, Cloneable {

    public static Settings getDefault() {
        return new Settings();
    }

    public static Settings of(Settings settings) {
        return new Settings(settings);
    }

    Settings() {
    }

    Settings(Settings settings) {
        this.basePath = settings.basePath;
        this.autoRegisterAllArt = settings.autoRegisterAllArt;
        this.modulePath = settings.modulePath;
    }

    private String basePath = "./";

    private String modulePath = "modules/";

    /**
     * If this is set to true, the ART-Framework will automatically scan the whole
     * classpath to find and register any {@link ArtObject}s that
     * have a parameterless public constructor.
     * <br>
     * Use the {@link ArtProvider} methods to register {@link ArtObject}s
     * that cannot be constructed so easily.
     */
    private boolean autoRegisterAllArt = true;
}

