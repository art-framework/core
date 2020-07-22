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

import lombok.SneakyThrows;
import net.silthus.art.*;
import net.silthus.art.conf.ActionConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@DisplayName("FlowParser")
class FlowParserTest {

    private FlowParser parser;
    private ArtObjectContextParser<?> flowParser;

    @BeforeEach
    @SneakyThrows
    void beforeEach() {

        Configuration configuration = mock(Configuration.class);

        flowParser = spy(new ArtObjectContextParser<ArtFactory<?, ?>>(configuration, new FlowType("test", ".")) {
            @Override
            protected Optional<ArtFactory<?, ?>> getFactory(String identifier) {
                ArtFactory<ArtObjectContext<?>, ?> artFactory = mock(ArtFactory.class);
                ArtInformation artInformation = mock(ArtInformation.class);
                when(artInformation.getConfigMap()).thenReturn(new HashMap());
                when(artFactory.info()).thenReturn(artInformation);
                ArtObjectContext context = mock(ArtObjectContext.class);
                when(artFactory.create(anyMap())).thenReturn(context);
                return Optional.of(artFactory);
            }

            @Override
            protected ConfigMap getGeneralConfigMap() {
                return ActionConfig.getConfigMap();
            }
        });

        ArrayList<net.silthus.art.FlowParser> parsers = new ArrayList<>();
        parsers.add(flowParser);

        FlowParserProvider parserProvider = mock(FlowParserProvider.class);
        when(configuration.parser()).thenReturn(parserProvider);
        when(parserProvider.all()).thenReturn(parsers);

        parser = new FlowParser(configuration);
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

        @Test
        @SneakyThrows
        @DisplayName("should call parser.parse() for every line")
        void shouldCallParseForEveryLine() {

            parser.parse(Arrays.asList(
                    "!foobar",
                    "!foo",
                    "?requirement",
                    "and more",
                    "foo",
                    "---"
            ));
            verify(flowParser, times(6)).parse();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all parsed contexts in the result")
        void shouldAddAllParsedContextsToTheResult() {

            assertThat(parser.parse(Arrays.asList(
                    "!foobar",
                    "!foo",
                    "?requirement",
                    "and more",
                    "foo",
                    "---"
            ))).extracting(ArtContext::getArtContexts)
                    .asList().hasSize(6);
        }

        @Test
        @SneakyThrows
        @DisplayName("should throw and display line number if parsing fails")
        void shouldThrowIfParsingALineFails() {

            ArtParseException exception = new ArtParseException("TEST ERROR");
            doAnswer((Answer<ArtObjectContext<?>>) invocation -> {
                ArtObjectContextParser<?> parser = (ArtObjectContextParser<?>) invocation.getMock();
                if ("ERROR".equals(parser.getInput())) {
                    throw exception;
                }
                return mock(ArtObjectContext.class);
            }).when(flowParser).parse();

            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(() -> parser.parse(Arrays.asList(
                            "!foobar",
                            "?req",
                            "ERROR",
                            "!foo",
                            "bar"
                    )))
                    .withMessage("TEST ERROR on ART line 3")
                    .withCause(exception);
        }

        @Test
        @DisplayName("should throw if line matches no parser")
        void shouldThrowIfLineHasNoMatchingParser() {

            doReturn(false).when(flowParser).accept("no-match");

            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(() -> parser.parse(Arrays.asList(
                            "!foobar",
                            "foo",
                            "no-match"
                    )))
                    .withMessage("Unable to find matching parser for \"no-match\" on line 3");
        }
    }
}