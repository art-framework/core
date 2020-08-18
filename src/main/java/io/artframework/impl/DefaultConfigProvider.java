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

import io.artframework.AbstractScoped;
import io.artframework.ConfigProvider;
import io.artframework.Scope;

import java.io.File;
import java.util.Optional;

public class DefaultConfigProvider extends AbstractScoped implements ConfigProvider {

    public DefaultConfigProvider(Scope scope) {
        super(scope);
    }

    @Override
    public <TConfig> Optional<TConfig> load(Class<TConfig> configClass, File file) {

        try {
            return Optional.of(configClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public <TConfig> void save(TConfig config, File file) {

    }
}
