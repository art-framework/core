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

package io.artframework.parser;

import com.google.common.base.Strings;
import io.artframework.*;
import io.artframework.conf.ConfigFieldInformation;
import io.artframework.conf.KeyValuePair;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The config parser is used to extract {@link ConfigMap} values from a string
 * and returns a new config map with the values.
 * <p>The parser will use the <a href="regexr.com/576km">following pattern</a> to parse the given input.
 */
@SuppressWarnings("RegExpRedundantEscape")
@Accessors(fluent = true)
public final class ConfigParser implements Scoped {

    public static ConfigParser of(Scope scope, ConfigMap configMap) {
        return new ConfigParser(scope, configMap);
    }

    // always edit the regexr link and update the link below!
    // the regexr link and the regex should always match
    // regexr.com/576km
    private static final Pattern PATTERN = Pattern.compile("^(?<keyValue>((?<key>[\\w\\d._-]+)?[:=])?((\"(?<quotedValue>.*?)\")|(\\[(?<array>.*?)\\])|(?<valueWithSpaces>(?<value>[^;, ]*)[,; ]?)))(?<config>.*)?$");

    @Getter
    private final Scope scope;
    @Getter
    private final ConfigMap configMap;
    private Matcher matcher;

    ConfigParser(Scope scope, ConfigMap configMap) {
        this.scope = scope;

        this.configMap = configMap;
    }

    /**
     * Accepts the given input and creates a matcher from it returning the result of the match.
     * <p>The last input will be used when calling {@link #parse()}.
     * It is required to call accept before parse.
     *
     * @param input the input that should be parsed
     * @return true if the config pattern matches and parse can be called
     */
    public boolean accept(String input) {

        if (Strings.isNullOrEmpty(input)) return false;

        this.matcher = PATTERN.matcher(input);

        return matcher.matches();
    }

    /**
     * Parses the last input passed to {@link #accept(String)} and returns
     * the config map of this parser with the values from the given input.
     * <p>{@link #accept(String)} must be called before calling parse.
     *
     * @return the {@link ConfigMap} with the values from the parsed input
     * @throws ParseException if the given input cannot be parsed into the config map
     */
    public ConfigMap parse() throws ParseException {

        if (matcher == null) throw new ParseException("ConfigParser not initialized! Call accept(String) first!");

        try {
            return configMap.with(scope(), extractKeyValuePairs());
        } catch (ConfigurationException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public List<KeyValuePair> extractKeyValuePairs() throws ParseException {

        if (matcher == null) throw new ParseException("ConfigParser not initialized! Call accept(String) first!");

        ArrayList<KeyValuePair> pairs = new ArrayList<>();

        String quotedValue = matcher.group("quotedValue");
        String array = matcher.group("array");
        String unquotedValue = matcher.group("value");
        String value;

        if (quotedValue != null) {
            value = quotedValue;
        } else if (array != null) {
            value = array;
        } else {
            value = unquotedValue;
        }

        String config = matcher.group("config");
        if (Strings.isNullOrEmpty(array) && configMap().configFields().size() == 1
                && configMap().configFields().values().stream().findFirst()
                .map(ConfigFieldInformation::type)
                .map(Class::isArray)
                .orElse(false)) {

            if (Strings.isNullOrEmpty(quotedValue)) {
                value = matcher.group("valueWithSpaces") + config;
            } else {
                value = "\"" + quotedValue + "\"" + config;
            }

            pairs.add(KeyValuePair.of(matcher.group("key"), value));
            return pairs;
        }

        pairs.add(KeyValuePair.of(matcher.group("key"), value));

        if (!Strings.isNullOrEmpty(config)) {
            this.matcher = PATTERN.matcher(StringUtils.strip(config.trim(), ",;").trim());
            if (matcher.matches()) {
                pairs.addAll(extractKeyValuePairs());
            }
        }

        return pairs;
    }
}
