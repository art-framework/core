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

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import net.silthus.art.AbstractProvider;
import net.silthus.art.Configuration;
import net.silthus.art.FlowParser;
import net.silthus.art.FlowParserProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultFlowParserProvider extends AbstractProvider implements FlowParserProvider {

    private final List<FlowParser> flowParsers = new ArrayList<>();

    public DefaultFlowParserProvider(@NonNull Configuration configuration) {
        super(configuration);
    }

    @Override
    public Collection<FlowParser> all() {
        return ImmutableList.copyOf(flowParsers);
    }

    @Override
    public FlowParserProvider add(FlowParser parser) {
        flowParsers.add(parser);
        return this;
    }

    @Override
    public FlowParserProvider remove(FlowParser parser) {
        flowParsers.remove(parser);
        return this;
    }

    @Override
    public FlowParserProvider clear() {
        flowParsers.clear();
        return this;
    }
}
