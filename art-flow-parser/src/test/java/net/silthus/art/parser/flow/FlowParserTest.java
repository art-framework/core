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
import lombok.SneakyThrows;
import net.silthus.art.ActionContext;
import net.silthus.art.DefaultArtResult;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.parser.flow.parser.ArtTypeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("FlowParser")
class FlowParserTest {

    private ArtManager artManager;
    private FlowParser parser;
    private ArtTypeParser artTypeParser;
    private final Set<Provider<ArtTypeParser<?, ?>>> parsers = new HashSet<>();

    @BeforeEach
    void beforeEach() {
        artManager = mock(ArtManager.class);
        when(artManager.getGlobalFilters()).thenReturn(new HashMap<>());

        Provider<ArtTypeParser<?, ?>> actionParserProvider = mock(Provider.class);
        artTypeParser = mock(ArtTypeParser.class);
        when(actionParserProvider.get()).thenReturn(artTypeParser);
        parsers.add(actionParserProvider);

        parser = new FlowParser(artManager, DefaultArtResult::new, parsers);
    }

    @Nested
    @DisplayName("parse(ArtConfig)")
    class parse {

        @Test
        @DisplayName("should throw if config object is null")
        public void shouldThrowIfObjectIsNull() {
            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> parser.parse(null));
        }

        @Nested
        @DisplayName("with a list of actions")
        class PureActionConfig {

            private ArtConfig config;

            @BeforeEach
            @SneakyThrows
            void beforeEach() {
                config = new ArtConfig();
                config.getArt().addAll(List.of(
                        "!foobar",
                        "!action",
                        "!gogo"
                ));
                when(artTypeParser.parse()).thenReturn(mock(ActionContext.class));
            }
        }
    }
}