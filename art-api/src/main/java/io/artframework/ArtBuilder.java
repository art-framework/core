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

package io.artframework;

import io.artframework.impl.DefaultArtBuilder;
import io.artframework.parser.flow.FlowParser;

import java.util.Collection;

public interface ArtBuilder extends Scope {

    ArtBuilder DEFAULT = of(ART.configuration());

    static ArtBuilder of(Configuration configuration) {
        return new DefaultArtBuilder(configuration);
    }

    <TParser extends Parser<TInput>, TInput> ArtBuilderParser<TParser, TInput> parser(TParser parser);

    ArtBuilderParser<FlowParser, Collection<String>> parser();

    default ArtBuilderParser<FlowParser, Collection<String>> load(Collection<String> strings) {
        return parser().parse(strings);
    }

    ArtContext build();
}
