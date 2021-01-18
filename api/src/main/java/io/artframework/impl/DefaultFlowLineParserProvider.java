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

import io.artframework.AbstractProvider;
import io.artframework.Scope;
import io.artframework.parser.flow.FlowLineParser;
import io.artframework.parser.flow.FlowLineParserProvider;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DefaultFlowLineParserProvider extends AbstractProvider implements FlowLineParserProvider {

    private final List<BiFunction<Iterator<String>, Scope, FlowLineParser>> flowLineParsers = new ArrayList<>();

    public DefaultFlowLineParserProvider(@NonNull Scope scope) {
        super(scope);
        flowLineParsers.addAll(FlowLineParser.defaults());
    }

    @Override
    public Collection<FlowLineParser> all(Iterator<String> iterator, Scope scope) {

        return flowLineParsers.stream()
                .map(supplier -> supplier.apply(iterator, scope))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public FlowLineParserProvider add(BiFunction<Iterator<String>, Scope, FlowLineParser> parser) {
        flowLineParsers.add(parser);
        return this;
    }

    @Override
    public FlowLineParserProvider clear() {
        flowLineParsers.clear();
        return this;
    }
}
