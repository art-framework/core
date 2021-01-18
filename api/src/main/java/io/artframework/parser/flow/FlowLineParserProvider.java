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

import io.artframework.Provider;
import io.artframework.Scope;
import io.artframework.impl.DefaultFlowLineParserProvider;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiFunction;

/**
 * The flow line parser provider allows the registration of custom parsers
 * that are iterated when parsing a list of flow lines.
 * <p>Register an implemented {@link FlowLineParser} with this provider and they are
 * automatically used when a {@code List<String>} is parsed by the {@link FlowParser}.
 */
public interface FlowLineParserProvider extends Provider {

    static FlowLineParserProvider of(Scope scope) {
        return new DefaultFlowLineParserProvider(scope);
    }

    /**
     * Creates a new instance of all registered parsers for the given iterator and scope.
     *
     * @param iterator the iterator of the parse operation
     * @param scope the scope
     * @return an immutable list of {@link FlowLineParser}s
     */
    Collection<FlowLineParser> all(Iterator<String> iterator, Scope scope);

    /**
     * Registers the given flow line parser in this provider.
     *
     * @param parser the function that can create a new instance of the parser
     * @return this provider
     */
    FlowLineParserProvider add(BiFunction<Iterator<String>, Scope, FlowLineParser> parser);

    /**
     * Clears all registered flow parsers including the default parsers.
     * <p>Make sure to add them back using the {@link FlowLineParser#defaults()} list.
     *
     * @return this provider
     */
    FlowLineParserProvider clear();
}
