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

import io.artframework.*;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class FlowParser implements Parser<Collection<String>> {

    private final Configuration configuration;

    public FlowParser(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public @NonNull Configuration configuration() {
        return configuration;
    }

    @Override
    public ArtContext parse(Collection<String> input) throws ParseException {

        Collection<ArtObjectContext<?>> contexts = new ArrayList<>();

        Collection<io.artframework.FlowParser> parsers = configuration().parser().all();

        int lineCount = 1;
        for (String line : input) {
            boolean matched = false;
            for (io.artframework.FlowParser parser : parsers) {
                try {
                    if (parser.accept(line)) {
                        matched = true;
                        contexts.add(parser.parse());
                        break;
                    }
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage() + " on ART line " + lineCount + "/" + input.size(), e);
                }
            }

            if (!matched) {
                throw new ParseException("Unable to find matching FlowParser for \"" + line + "\" on line " + lineCount + "/" + input.size());
            }

            lineCount++;
        }

        contexts = sortAndCombineArtContexts(contexts.stream().filter(Objects::nonNull).collect(Collectors.toList()));

        return ArtContext.of(configuration, configuration.settings().artSettings(), contexts);
    }

    // rules for matching and combining actions, requirements and trigger
    // - requirements can neither have actions nor trigger
    // - actions can have requirements that are checked before execution and nested actions that are executed in sequence after the first action
    // - trigger can have requirements and execute actions
    Collection<ArtObjectContext<?>> sortAndCombineArtContexts(Collection<ArtObjectContext<?>> contexts) {

        return FlowLogicSorter.of(contexts).getResult();
    }
}
