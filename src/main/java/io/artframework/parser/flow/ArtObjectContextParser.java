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
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Accessors(fluent = true)
@SuppressWarnings("RegExpRedundantEscape")
public abstract class ArtObjectContextParser<TFactory extends Factory<?, ?>> extends LineParser<ArtObjectContext<?>> implements FlowParser {

    @Getter
    private final FlowType flowType;

    protected ArtObjectContextParser(Configuration configuration, FlowType flowType) {
        // always edit the regexr link and update the link below!
        // the regexr link and the regex should always match
        // regexr.com/56s09
        super(configuration, Pattern.compile("^" + flowType.typeIdentifier() + "(?<identifier>[\\w\\d:._-]+)([\\[\\(](?<config>[^\\]\\)]*?)[\\]\\)])?( (?<userConfig>.+))?$"));
        this.flowType = flowType;
    }

    protected String getIdentifier() {
        return matcher().group("identifier");
    }

    protected Optional<String> getConfig() {
        String config = matcher().group("config");
        if (Strings.isNullOrEmpty(config)) return Optional.empty();
        return Optional.of(config);
    }

    protected String userConfig() {
        return matcher().group("userConfig");
    }

    protected abstract Optional<TFactory> factory(String identifier);

    protected abstract ConfigMap configMap();

    @Override
    public ArtObjectContext<?> parse() throws ParseException {

        String identifier = getIdentifier();
        Optional<TFactory> factoryOptional = factory(identifier);

        if (!factoryOptional.isPresent()) {
            throw new ParseException("No " + this.flowType().name() + " with identifier \"" + identifier + "\" found");
        }

        TFactory factory = factoryOptional.get();
        Map<ConfigMapType, ConfigMap> configMaps = new HashMap<>();

        Optional<String> config = getConfig();
        if (config.isPresent()) {
            ConfigMap configMap = configMap();
            ConfigParser configParser = ConfigParser.of(this.configuration(), configMap);
            if (configParser.accept(config.get())) {
                configMaps.put(configMap.type(), configParser.parse());
            }
        }


        ConfigParser configParser = ConfigParser.of(this.configuration(), ConfigMap.of(ConfigMapType.ART_CONFIG, factory.meta().configMap()));
        if (configParser.accept(userConfig())) {
            configMaps.put(ConfigMapType.ART_CONFIG, configParser.parse());
        }

        return factory.create(configMaps);
    }
}
