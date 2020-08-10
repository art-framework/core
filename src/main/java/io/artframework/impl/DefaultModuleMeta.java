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

package io.artframework.impl;

import com.google.common.base.Strings;
import io.artframework.ModuleMeta;
import io.artframework.annotations.ART;
import io.artframework.annotations.Depends;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;

@Value
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"identifier", "moduleClass"})
public class DefaultModuleMeta implements ModuleMeta {

    String identifier;
    Class<?> moduleClass;
    String version;
    String[] alias;
    String[] description;
    String[] dependencies;
    String[] pluginDependencies;

    public DefaultModuleMeta(@NonNull String identifier,
                             @NonNull Class<?> moduleClass,
                             @Nullable String version,
                             @Nullable String[] alias,
                             @Nullable String[] description,
                             @Nullable String[] dependencies,
                             @Nullable String[] pluginDependencies) {

        this.identifier = identifier;
        this.moduleClass = moduleClass;
        this.version = Strings.isNullOrEmpty(version) ? "1.0.0" : version;
        this.alias = alias == null ? new String[0] : alias;
        this.description = description == null ? new String[0] : description;
        this.dependencies = dependencies == null ? new String[0] : dependencies;
        this.pluginDependencies = pluginDependencies == null ? new String[0] : pluginDependencies;
    }

    public DefaultModuleMeta(@NonNull Class<?> moduleClass,
                             @NonNull ART art,
                             @Nullable Depends depends) {
        this(art.value(),
                moduleClass,
                art.version(),
                art.alias(),
                art.description(),
                depends == null ? null : depends.modules(),
                depends == null ? null : depends.plugins()
        );
    }
}
