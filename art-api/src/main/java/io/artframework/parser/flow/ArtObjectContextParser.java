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

package io.artframework.parser.flow;

import com.google.common.base.Strings;
import io.artframework.FlowParser;
import io.artframework.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class ArtObjectContextParser<TFactory extends ArtFactory<?, ?>> extends LineParser<ArtObjectContext<?>> implements FlowParser {

    @Getter
    private final FlowType flowType;

    protected ArtObjectContextParser(Configuration configuration, FlowType flowType) {
        // always edit the regexr link and update the link below!
        // the regexr link and the regex should always match
        // regexr.com/56s09
        super(configuration, Pattern.compile("^" + flowType.getTypeIdentifier() + "(?<identifier>[\\w\\d:._-]+)([\\[\\(](?<config>[^\\]\\)]*?)[\\]\\)])?( (?<userConfig>.+))?$"));
        this.flowType = flowType;
    }

    protected String getIdentifier() {
        return getMatcher().group("identifier");
    }

    protected Optional<String> getConfig() {
        String config = getMatcher().group("config");
        if (Strings.isNullOrEmpty(config)) return Optional.empty();
        return Optional.of(config);
    }

    protected String getUserConfig() {
        return getMatcher().group("userConfig");
    }

    protected abstract Optional<TFactory> getFactory(String identifier);

    protected abstract ConfigMap getGeneralConfigMap();

    @Override
    public ArtObjectContext<?> parse() throws ArtParseException {

        String identifier = getIdentifier();
        Optional<TFactory> factoryOptional = getFactory(identifier);

        if (!factoryOptional.isPresent()) {
            throw new ArtParseException("No " + this.getFlowType().getName() + " with identifier \"" + identifier + "\" found");
        }

        TFactory factory = factoryOptional.get();
        Map<ConfigMapType, ConfigMap> configMaps = new HashMap<>();

        Optional<String> config = getConfig();
        if (config.isPresent()) {
            ConfigMap configMap = getGeneralConfigMap();
            ConfigParser configParser = ConfigParser.of(this.getConfiguration(), configMap);
            if (configParser.accept(config.get())) {
                configMaps.put(configMap.getType(), configParser.parse());
            }
        }


        ConfigParser configParser = ConfigParser.of(this.getConfiguration(), ConfigMap.of(ConfigMapType.SPECIFIC_ART_CONFIG, factory.options().getConfigMap()));
        if (configParser.accept(getUserConfig())) {
            configMaps.put(ConfigMapType.SPECIFIC_ART_CONFIG, configParser.parse());
        }

        return factory.create(configMaps);
    }
}
