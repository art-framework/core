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

package net.silthus.art.parser.flow;

import com.google.inject.Provider;
import net.silthus.art.Parser;
import net.silthus.art.*;
import net.silthus.art.parser.flow.parser.ArtTypeParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FlowParser implements Parser<List<String>> {

    private final Configuration configuration;

    public FlowParser(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ArtContext parse(List<String> input) throws ArtParseException {

        Collection<ArtObjectContext<?>> contexts = new ArrayList<>();

        List<? extends ArtTypeParser<?, ?>> parsers = this.parsers.stream().map(Provider::get).collect(Collectors.toList());

        int lineCount = 1;
        for (String line : input) {
            boolean matched = false;
            for (ArtTypeParser<?, ?> parser : parsers) {
                try {
                    if (parser.accept(line)) {
                        matched = true;
                        contexts.add(parser.parse());
                        break;
                    }
                } catch (ArtParseException e) {
                    throw new ArtParseException(e.getMessage() + " on ART line " + lineCount, e);
                }
            }
            if (!matched) {
                throw new ArtParseException("Unable to find matching parser for \"" + line + "\" on line " + lineCount);
            }
            lineCount++;
        }

        contexts = sortAndCombineArtContexts(contexts);

        return ArtContext.of(configuration, contexts);
    }

    // rules for matching and combining actions, requirements and trigger
    // - requirements can neither have actions nor trigger
    // - actions can have requirements that are checked before execution and nested actions that are executed in sequence after the first action
    // - trigger can have requirements and execute actions
    Collection<ArtObjectContext<?>> sortAndCombineArtContexts(Collection<ArtObjectContext<?>> contexts) {

        return FlowLogicSorter.of(contexts).getResult();
    }
}
