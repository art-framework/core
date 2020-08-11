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

import io.artframework.ModuleMeta;
import io.artframework.annotations.ArtModule;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"identifier", "moduleClass"})
public class DefaultModuleMeta implements ModuleMeta {

    String identifier;
    String prefix;
    Class<?> moduleClass;
    String version;
    String[] description;
    String[] dependencies;
    String[] packages;

    DefaultModuleMeta(@NonNull String identifier,
                      @NonNull String prefix,
                      @NonNull Class<?> moduleClass,
                      @NonNull String version,
                      @NonNull String[] description,
                      @NonNull String[] dependencies,
                      @NonNull String[] packages) {

        this.identifier = identifier;
        this.prefix = prefix;
        this.moduleClass = moduleClass;
        this.version = version;
        this.description = description;
        this.dependencies = dependencies;
        this.packages = packages.length < 1 ? new String[] {moduleClass.getPackage().getName()} : packages;
    }

    public DefaultModuleMeta(@NonNull Class<?> moduleClass,
                             @NonNull ArtModule annotation) {
        this(
                annotation.identifier(),
                annotation.prefix(),
                moduleClass,
                annotation.version(),
                annotation.description(),
                annotation.dependencies(),
                annotation.packages()
        );
    }
}
