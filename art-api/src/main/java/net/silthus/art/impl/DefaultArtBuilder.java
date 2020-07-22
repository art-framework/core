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
import net.silthus.art.parser.flow.FlowParser;

import java.util.List;

public class DefaultArtBuilder implements ArtBuilder {

    private final Configuration configuration;

    public DefaultArtBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <TParser extends Parser<TInput>, TInput> ArtBuilderParser<TParser, TInput> parser(TParser parser) {
        return null;
    }

    @Override
    public ArtBuilderParser<FlowParser, List<String>> parser() {
        return new DefaultArtBuilderParser<>(getConfiguration(), new FlowParser(getConfiguration()));
    }

    @Override
    public ArtContext build() {
        return null;
    }
}
