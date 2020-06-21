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
import net.silthus.art.AbstractArtResult;
import net.silthus.art.api.ArtContext;
import net.silthus.art.parser.flow.parser.ArtTypeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("FlowParser")
class FlowParserTest {

    private FlowParser parser;
    private final Set<Provider<ArtTypeParser<?, ?>>> parsers = new HashSet<>();

    @BeforeEach
    void beforeEach() {
        parser = new FlowParser((config, artContexts) -> new AbstractArtResult(config, artContexts) {
            @Override
            protected <TTarget> boolean filter(TTarget target, ArtContext<TTarget, ?> context) {
                return false;
            }
        }, parsers);
    }

    @Nested
    @DisplayName("next(Object)")
    class parse {

        @Test
        @DisplayName("should throw if config object is null")
        public void shouldThrowIfObjectIsNull() {
            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> parser.parse(null));
        }
    }
}