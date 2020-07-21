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

package net.silthus.art.impl;

import net.silthus.art.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultArtBuilderParser<TParser extends Parser<TInput>, TInput> extends DefaultArtBuilder implements ArtBuilderParser<TParser, TInput> {

    private final TParser parser;
    private final List<TInput> inputs = new ArrayList<>();

    public DefaultArtBuilderParser(Configuration configuration, TParser parser) {
        super(configuration);
        this.parser = parser;
    }

    @Override
    public ArtBuilderParser<TParser, TInput> load(TInput input) {
        inputs.add(input);
        return this;
    }

    @Override
    public ART build() {

        return inputs.stream()
                .map(input -> {
                    try {
                        return parser.parse(input);
                    } catch (ArtParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .reduce(ART::combine)
                .orElse(ART.empty());
    }
}
