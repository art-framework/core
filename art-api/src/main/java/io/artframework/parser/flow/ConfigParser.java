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
import io.artframework.ArtConfigException;
import io.artframework.ArtParseException;
import io.artframework.ConfigMap;
import io.artframework.Configuration;
import io.artframework.conf.KeyValuePair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigParser extends LineParser<ConfigMap> {

    public static ConfigParser of(Configuration configuration, ConfigMap configMap) {
        return new ConfigParser(configuration, configMap);
    }

    @Getter
    private final ConfigMap configMap;

    protected ConfigParser(Configuration configuration, ConfigMap configMap) {
        // always edit the regexr link and update the link below!
        // the regexr link and the regex should always match
        // regexr.com/576km
        super(configuration, Pattern.compile("^(?<keyValue>((?<key>[\\w\\d._-]+)?[:=])?((\"(?<quotedValue>.*?)\")|((?<value>[^;, ]*)[,; ]?)))(?<config>.*)?$"));
        this.configMap = configMap;
    }

    @Override
    public ConfigMap parse() throws ArtParseException {

        try {
            return configMap.loadValues(extractKeyValuePairs(getMatcher()));
        } catch (ArtConfigException e) {
            throw new ArtParseException(e.getMessage(), e);
        }
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
}
