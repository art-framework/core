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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.artframework.AbstractScoped;
import io.artframework.ConfigProvider;
import io.artframework.Scope;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class JacksonConfigProvider extends AbstractScoped implements ConfigProvider {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    public JacksonConfigProvider(Scope scope) {
        super(scope);
    }

    @Override
    public <TConfig> Optional<TConfig> load(Class<TConfig> configClass, File file) {

        if (!file.exists()) {
            try {
                save(scope().configuration().injector().create(configClass, scope()), file, false);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        try {
            return Optional.of(mapper.readValue(file, configClass));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public <TConfig> void save(TConfig config, File file) {

        save(config, file, true);
    }

    private <TConfig> void save(TConfig config, File file, boolean overwrite) {

        if (file.exists() && !overwrite) return;

        try {
            mapper.writeValue(file, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
