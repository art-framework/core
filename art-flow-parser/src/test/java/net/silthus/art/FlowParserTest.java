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

package net.silthus.art;

import com.google.inject.Provider;
import lombok.SneakyThrows;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.parser.flow.parser.ArtTypeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@DisplayName("FlowParser")
@SuppressWarnings({"unchecked", "rawtypes"})
class FlowParserTest {

    private ArtManager artManager;
    private FlowParser parser;
    private ArtTypeParser artTypeParser;
    private final Set<Provider<ArtTypeParser<?, ?>>> parsers = new HashSet<>();

    @BeforeEach
    @SneakyThrows
    void beforeEach() {
        artManager = mock(ArtManager.class);
        when(artManager.getGlobalFilters()).thenReturn(new HashMap<>());

        artTypeParser = mock(ArtTypeParser.class);
        when(artTypeParser.getPattern()).thenReturn(Pattern.compile(".*"));
        when(artTypeParser.getMatcher()).thenCallRealMethod();
        when(artTypeParser.getInput()).thenCallRealMethod();
        when(artTypeParser.accept(anyString())).thenCallRealMethod();
        ArtContext artContext = mock(ArtContext.class);
        when(artContext.getOptions()).thenReturn(new ArtObjectConfig());
        when(artTypeParser.parse()).thenReturn(artContext);

        Provider<ArtTypeParser<?, ?>> actionParserProvider = mock(Provider.class);
        when(actionParserProvider.get()).thenReturn(this.artTypeParser);

        parsers.add(actionParserProvider);

        parser = spy(new FlowParser(artManager, DefaultArtResult::new, parsers));
    }

    @Nested
    @DisplayName("parse(ArtConfig)")
    class parse {

        private ArtConfig config;

        @BeforeEach
        @SneakyThrows
        void beforeEach() {
            config = new ArtConfig();
        }

        private void addLines(String... lines) {
            config.getArt().addAll(Arrays.asList(lines));
        }

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

            addLines(
                    "!foobar",
                    "!foo",
                    "?requirement",
                    "and more",
                    "foo",
                    "---"
            );

            parser.parse(config);
            verify(artTypeParser, times(6)).parse();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all parsed contexts in the result")
        void shouldAddAllParsedContextsToTheResult() {

            when(parser.sortAndCombineArtContexts(anyList())).then(invocation -> invocation.getArgument(0));

            addLines(
                    "!foobar",
                    "!foo",
                    "?requirement",
                    "and more",
                    "foo",
                    "---"
            );

            assertThat(parser.parse(config))
                    .extracting("art.size")
                    .isEqualTo(6);
        }

        @Test
        @SneakyThrows
        @DisplayName("should throw and display line number if parsing fails")
        void shouldThrowIfParsingALineFails() {

            ArtParseException exception = new ArtParseException("TEST ERROR");
            doAnswer((Answer<ArtContext<?, ?, ?>>) invocation -> {
                ArtTypeParser<?, ?> parser = (ArtTypeParser<?, ?>) invocation.getMock();
                if ("ERROR".equals(parser.getInput())) {
                    throw exception;
                }
                return mock(ArtContext.class);
            }).when(artTypeParser).parse();

            addLines(
                    "!foobar",
                    "?req",
                    "ERROR",
                    "!foo",
                    "bar"
            );

            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(() -> parser.parse(config))
                    .withMessage("TEST ERROR on ART line 3")
                    .withCause(exception);
        }

        @Test
        @DisplayName("should throw if line matches no parser")
        void shouldThrowIfLineHasNoMatchingParser() {

            doReturn(false).when(artTypeParser).accept("no-match");

            addLines(
                    "!foobar",
                    "foo",
                    "no-match"
            );

            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(() -> parser.parse(config))
                    .withMessage("Unable to find matching parser for \"no-match\" on line 3");
        }
    }
}