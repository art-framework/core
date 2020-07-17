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
import lombok.Getter;
import net.silthus.art.ArtConfigException;
import net.silthus.art.ArtParseException;
import net.silthus.art.ConfigMap;
import net.silthus.art.conf.KeyValuePair;
import net.silthus.art.parser.flow.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigParser extends Parser<ConfigMap> {

    @Getter
    private final ConfigMap configMap;

    public ConfigParser(ConfigMap configMap) {
        // always edit the regexr link and update the link below!
        // the regexr link and the regex should always match
        // regexr.com/576km
        super(Pattern.compile("^(?<keyValue>((?<key>[\\w\\d._-]+)?[:=])?((\"(?<quotedValue>.*?)\")|((?<value>[^;, ]*)[,; ]?)))(?<config>.*)?$"));
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
