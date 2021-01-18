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

import io.artframework.*;
import io.artframework.parser.flow.FlowParser;
import io.artframework.util.ReflectionUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Log(topic = "art-framework")
public class DefaultArtLoader implements ArtLoader {

    @Getter
    @Accessors(fluent = true)
    private final Scope scope;
    private final Map<Class<?>, Function<Scope, ? extends Parser<?>>> parsers = new HashMap<>();

    public DefaultArtLoader(Scope scope) {
        this.scope = scope;

        parser(FlowParser.class, FlowParser::new);
    }

    @Override
    public <TParser extends Parser<TInput>, TInput> ArtLoader parser(Class<TParser> parserClass, Function<Scope, TParser> parser) {

        parsers.put(parserClass, parser);
        log.info("registered art-parser: " + parserClass.getCanonicalName());

        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TParser extends Parser<TInput>, TInput> TParser parser(Class<TParser> parserClass) {

        Parser<?> parser = ReflectionUtil.getEntryForTargetClass(parserClass, parsers)
                .map(scopeFunction -> scopeFunction.apply(scope))
                .orElse(null);

        if (parser == null) return null;

        return (TParser) parser;
    }
}
