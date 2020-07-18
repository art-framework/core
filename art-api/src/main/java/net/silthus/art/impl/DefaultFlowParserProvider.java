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

import lombok.NonNull;
import net.silthus.art.AbstractProvider;
import net.silthus.art.Configuration;
import net.silthus.art.FlowParserProvider;
import net.silthus.art.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class DefaultFlowParserProvider extends AbstractProvider implements FlowParserProvider {

    private final Map<Class<?>, Supplier<Parser<?>>> parsers = new HashMap<>();

    public DefaultFlowParserProvider(@NonNull Configuration configuration) {
        super(configuration);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TInput> Optional<Parser<TInput>> get(Class<TInput> inputClass) {
        return Optional.ofNullable(parsers.get(inputClass))
                .map(Supplier::get)
                .map(parser -> (Parser<TInput>) parser);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <TInput> FlowParserProvider add(Class<TInput> inputType, Supplier<Parser<TInput>> parserSupplier) {
        parsers.put(inputType, (Supplier) parserSupplier);
        return this;
    }

    @Override
    public FlowParserProvider remove(Class<?> inputType) {
        parsers.remove(inputType);
        return this;
    }
}
