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

import io.artframework.ArtContext;
import io.artframework.ArtParser;
import io.artframework.ParseException;
import io.artframework.Scope;
import io.artframework.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultArtParser<TParser extends Parser<TInput>, TInput> extends DefaultArtLoader implements ArtParser<TParser, TInput> {

    private final TParser parser;
    private final List<TInput> inputs = new ArrayList<>();

    public DefaultArtParser(Scope scope, TParser parser) {
        super(scope);
        this.parser = parser;
    }

    @Override
    public ArtParser<TParser, TInput> add(TInput input) {
        inputs.add(input);
        return this;
    }

    @Override
    public ArtContext parse() {

        return inputs.stream()
                .map(input -> {
                    try {
                        return parser.parse(input);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .reduce(ArtContext::combine)
                .orElse(ArtContext.empty());
    }
}
