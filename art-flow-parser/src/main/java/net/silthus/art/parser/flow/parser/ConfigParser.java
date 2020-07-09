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

package net.silthus.art.parser.flow.parser;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.Getter;
import net.silthus.art.api.config.ConfigFieldInformation;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.parser.flow.Parser;
import net.silthus.art.util.ConfigUtil;
import net.silthus.art.util.ReflectionUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigParser extends Parser<ConfigParser.Result> {

    @Getter
    private final Map<String, ConfigFieldInformation> configMap;

    public ConfigParser(Map<String, ConfigFieldInformation> configMap) {
        // always edit the regexr link and update the link below!
        // the regexr link and the regex should always match
        // regexr.com/576km
        super(Pattern.compile("^(?<keyValue>((?<key>[\\w\\d._-]+)?[:=])?((\"(?<quotedValue>.*?)\")|((?<value>[^;, ]*)[,; ]?)))(?<config>.*)?$"));
        this.configMap = ImmutableMap.copyOf(configMap);
    }

    @Override
    public Result parse() throws ArtParseException {

        Map<ConfigFieldInformation, Object> fieldValueMap = new HashMap<>();
        List<KeyValuePair> keyValuePairs = extractKeyValuePairs(getMatcher());
        Set<ConfigFieldInformation> mappedFields = new HashSet<>();

        boolean usedKeyValue = false;

        for (int i = 0; i < keyValuePairs.size(); i++) {
            KeyValuePair keyValue = keyValuePairs.get(i);
            ConfigFieldInformation configFieldInformation;
            if (keyValue.getKey().isPresent() && getConfigMap().containsKey(keyValue.getKey().get())) {
                configFieldInformation = getConfigMap().get(keyValue.getKey().get());
                usedKeyValue = true;
            } else if (getConfigMap().size() == 1) {
                //noinspection OptionalGetWithoutIsPresent
                configFieldInformation = getConfigMap().values().stream().findFirst().get();
            } else {
                if (usedKeyValue) {
                    throw new ArtParseException("Positioned parameter found after key=value pair usage. Positioned parameters must come first.");
                }
                int finalI = i;
                Optional<ConfigFieldInformation> optionalFieldInformation = getConfigMap().values().stream().filter(info -> info.getPosition() == finalI).findFirst();
                if (!optionalFieldInformation.isPresent()) {
                    throw new ArtParseException("Config does not define positioned parameters. Use key value pairs instead.");
                }
                configFieldInformation = optionalFieldInformation.get();
            }

            if (!keyValue.getValue().isPresent()) {
                throw new ArtParseException("Config " + configFieldInformation.getIdentifier() + " has an empty value.");
            }

            Object value = ReflectionUtil.toObject(configFieldInformation.getType(), keyValue.getValue().get());

            fieldValueMap.put(configFieldInformation, value);
            mappedFields.add(configFieldInformation);
        }

        List<ConfigFieldInformation> missingRequiredFields = getConfigMap().values().stream()
                .filter(ConfigFieldInformation::isRequired)
                .filter(configFieldInformation -> !mappedFields.contains(configFieldInformation))
                .collect(Collectors.toList());

        if (!missingRequiredFields.isEmpty()) {
            throw new ArtParseException("Config is missing " + missingRequiredFields.size() + " required parameters: "
                    + missingRequiredFields.stream().map(ConfigFieldInformation::getIdentifier).collect(Collectors.joining(",")));
        }

        return new Result(fieldValueMap);
    }

    protected List<KeyValuePair> extractKeyValuePairs(Matcher matcher) {

        ArrayList<KeyValuePair> pairs = new ArrayList<>();

        String quotedValue = matcher.group("quotedValue");
        String unqotedValue = matcher.group("value");
        String value = Strings.isNullOrEmpty(unqotedValue) ? quotedValue : unqotedValue;

        pairs.add(new KeyValuePair(matcher.group("key"), value));

        String config = matcher.group("config");
        if (!Strings.isNullOrEmpty(config)) {
            matcher = getPattern().matcher(config.trim());
            if (matcher.matches()) {
                pairs.addAll(extractKeyValuePairs(matcher));
            }
        }

        return pairs;
    }

    @Data
    static class KeyValuePair {

        private final String key;
        private final String value;

        public Optional<String> getKey() {
            return Optional.ofNullable(key);
        }

        public Optional<String> getValue() {
            return Optional.ofNullable(value);
        }
    }

    public static class Result {

        private final Map<ConfigFieldInformation, Object> keyValueMap;

        private Result(Map<ConfigFieldInformation, Object> keyValueMap) {
            this.keyValueMap = ImmutableMap.copyOf(keyValueMap);
        }

        public <TConfig> TConfig applyTo(TConfig config) {
            ConfigUtil.setConfigFields(config, keyValueMap);
            return config;
        }
    }

}
