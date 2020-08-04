/*
 *  Copyright 2020 ART-Framework Contributors (https://github.com/art-framework/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.artframework.impl;

import io.artframework.ModuleMeta;
import io.artframework.annotations.ART;
import io.artframework.annotations.Depends;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;

@Value
@Accessors(fluent = true)
public class DefaultModuleMeta implements ModuleMeta {

    String identifier;
    String version;
    String[] alias;
    String[] description;
    String[] dependencies;

    public DefaultModuleMeta(String identifier, String version, String[] alias, String[] description, String[] dependencies) {

        this.identifier = identifier;
        this.version = version;
        this.alias = alias;
        this.description = description;
        this.dependencies = dependencies;
    }

    public DefaultModuleMeta(@NonNull ART art, @Nullable Depends depends) {
        this.identifier = art.value();
        this.version = art.version();
        this.alias = art.alias();
        this.description = art.description();
        this.dependencies = depends != null ? depends.value() : new String[0];
    }
}
