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

import io.artframework.ArtContext;
import io.artframework.ArtObjectContext;
import io.artframework.ParseException;
import io.artframework.Scope;
import io.artframework.parser.Parser;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

@Accessors(fluent = true)
public final class FlowParser implements Parser<Collection<String>> {

    private final Scope scope;
    @Setter
    @Getter
    private String storageKey = UUID.randomUUID().toString();

    public FlowParser(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope scope() {
        return scope;
    }

    @Override
    public ArtContext parse(@NonNull Collection<String> input) throws ParseException {

        if (input.isEmpty()) {
            return ArtContext.empty();
        }

        Collection<ArtObjectContext<?>> contexts = new ArrayList<>();

        Iterator<String> iterator = input.iterator();
        Collection<FlowLineParser> parsers = configuration().parser().all(iterator, scope);

        int lineCount = 1;
        while (iterator.hasNext()) {
            String line = iterator.next();
            boolean matched = false;

            for (FlowLineParser parser : parsers) {
                try {
                    if (parser.accept(line)) {
                        matched = true;
                        contexts.add(parser.parse().storageKey(storageKey()));
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

        return ArtContext.of(scope, scope().settings().artSettings(), contexts);
    }

    Collection<ArtObjectContext<?>> sortAndCombineArtContexts(Collection<ArtObjectContext<?>> contexts) {

        return FlowLogicSorter.of(contexts).getResult();
    }
}
