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

import io.artframework.impl.DefaultArtLoader;
import io.artframework.parser.Parser;
import io.artframework.parser.flow.FlowParser;

import java.util.Collection;
import java.util.function.Function;

/**
 * The art loader is responsible for loading a config type into an art context.
 * <p>It can be configured with various parsers that parse the input into an {@link ArtContext}.
 */
public interface ArtLoader extends Scoped {

    /**
     * Creates a new art loader for the given scope.
     *
     * @param scope the scope to create a new art loader for
     * @return the created art loader
     */
    static ArtLoader of(Scope scope) {
        return new DefaultArtLoader(scope);
    }

    /**
     * Registers the given parser class and supplier as an {@link ArtParser} that can be used to create art contexts.
     * <p>Make sure that your implementing parser is immutable and only performs atomic operations without side effects.
     * <p>The same parser instance may be called with multiple inputs.
     *
     * @param parserClass the class of the parser that should be registered
     * @param parser the parser that is registered
     * @param <TParser> the type of the parser
     * @param <TInput> the input the parser accepts
     * @return this art loader instance
     */
    <TParser extends Parser<TInput>, TInput> ArtLoader parser(Class<TParser> parserClass, Function<Scope, TParser> parser);

    /**
     * Gets a registered parser for the given parser class.
     * <p>Returns null if no parser with the class is registered.
     *
     * @param parserClass the class of the parser
     * @param <TParser> the type of the parser
     * @param <TInput> the type of the input
     * @return a new instance of the given parser or null if no parser is registered
     */
    <TParser extends Parser<TInput>, TInput> TParser parser(Class<TParser> parserClass);

    /**
     * Gets the default {@link FlowParser} that can parse a list of strings into an art context
     * using the "flow syntax".
     *
     * @return the default flow parser
     */
    default FlowParser parser() {

        return parser(FlowParser.class);
    }

    /**
     * Directly parses the given string list using the default flow parser.
     *
     * @param strings the list of strings in flow syntax notation
     * @return the parsed {@link ArtContext}
     * @throws ParseException if the provided list of strings cannot be parsed into an art context.
     *                        <p>This can be the case if an art-object is missing or the syntax is incorrect.
     */
    default ArtContext parse(Collection<String> strings) throws ParseException {

        return parser().parse(strings);
    }
}
