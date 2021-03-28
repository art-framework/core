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
import io.artframework.BootstrapModule;
import io.artframework.Module;
import io.artframework.ModuleMeta;
import io.artframework.annotations.ArtModule;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Arrays;

@Value
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"identifier", "moduleClass"})
public class DefaultModuleMeta implements ModuleMeta {

    String identifier;
    String prefix;
    Class<? extends Module> moduleClass;
    String version;
    String[] description;
    String[] dependencies;
    boolean autoRegisterArt;
    String[] packages;
    boolean bootstrapModule;

    DefaultModuleMeta(@NonNull String identifier,
                      @NonNull String prefix,
                      @NonNull Class<? extends Module> moduleClass,
                      @NonNull String version,
                      @NonNull String[] description,
                      @NonNull String[] dependencies,
                      boolean autoRegisterArt,
                      @NonNull String[] packages) {

        this.identifier = Strings.isNullOrEmpty(identifier) ? moduleClass.getCanonicalName() : identifier;
        this.prefix = prefix;
        this.moduleClass = moduleClass;
        this.version = version;
        this.description = description;
        // TODO: remove the module: replacement when dependency hooks are implemented
        this.dependencies = Arrays.stream(dependencies).map(s -> s.replace("module:", "")).toArray(String[]::new);
        this.autoRegisterArt = autoRegisterArt;
        this.packages = packages.length == 0 ? new String[] {moduleClass.getPackageName()} : packages;
        this.bootstrapModule = BootstrapModule.class.isAssignableFrom(moduleClass);
    }

    public DefaultModuleMeta(@NonNull Class<? extends Module> moduleClass,
                             @NonNull ArtModule annotation) {
        this(
                annotation.value(),
                annotation.prefix(),
                moduleClass,
                annotation.version(),
                annotation.description(),
                annotation.depends(),
                annotation.autoRegisterArt(),
                annotation.packages());
    }
}
