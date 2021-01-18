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

import io.artframework.ArtObjectContext;
import io.artframework.Scope;
import io.artframework.Scoped;
import io.artframework.parser.LineParser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * The flow line parser takes a single String (line) and parses it into an art object context.
 * <p>You can provide your own flow parsers by implementing this interface and registering it
 * with the {@link FlowLineParserProvider} in the {@link io.artframework.annotations.OnBootstrap} phase.
 * <p>This is useful if you added new {@link io.artframework.ArtObject}s and need to provide a way
 * to parse the corresponding {@link ArtObjectContext} from an input.
 *
 * @see LineParser
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public abstract class FlowLineParser extends LineParser<ArtObjectContext<?>> implements Scoped {

    public static List<BiFunction<Iterator<String>, Scope, FlowLineParser>> defaults() {
        return Arrays.asList(
                ActionLineParser::new,
                RequirementLineParser::new,
                TriggerLineParser::new
        );
    }

    private final Scope scope;

    public FlowLineParser(Iterator<String> iterator, Scope scope, Pattern pattern) {

        super(iterator, pattern);
        this.scope = scope;
    }
}

