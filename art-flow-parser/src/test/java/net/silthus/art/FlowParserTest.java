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
import net.silthus.art.api.actions.ActionConfig;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.requirements.RequirementConfig;
import net.silthus.art.parser.flow.parser.ArtTypeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@DisplayName("FlowParser")
@SuppressWarnings("unchecked")
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
        when(artTypeParser.parse()).thenReturn(mock(ArtContext.class));

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

    @Nested
    @DisplayName("sortAndCombineArtContexts(...)")
    class sortAndCombine {

        private List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> contexts;

        @BeforeEach
        void beforeEach() {
            contexts = new ArrayList<>();
        }

        private ActionContext<?, ?> action() {
            return new ActionContext<>(Object.class, (o, context) -> {
            }, new ActionConfig<>());
        }

        private RequirementContext<?, ?> requirement() {
            return new RequirementContext<>(Object.class, (o, context) -> true, new RequirementConfig<>());
        }

        @Nested
        @DisplayName("with actions as result")
        class actions {

            @Test
            @DisplayName("should nest actions if requirements exist")
            void shouldNestActionsIfRequirementsExist() {

                ActionContext<?, ?> action = action();
                contexts.addAll(List.of(
                        requirement(),
                        requirement(),
                        action,
                        action(),
                        action()
                ));

                assertThat(parser.sortAndCombineArtContexts(contexts))
                        .containsExactly(action)
                        .extracting("childActions.size")
                        .contains(2);
            }

            @Test
            @DisplayName("should create new action if new requirements exist")
            void shouldCreateNewActionsIfNewRequirementsExist() {

                ActionContext<?, ?> firstAction = action();
                ActionContext<?, ?> secondAction = action();
                contexts.addAll(List.of(
                        requirement(),
                        firstAction,
                        requirement(),
                        secondAction
                ));

                assertThat(parser.sortAndCombineArtContexts(contexts))
                        .containsExactly(firstAction, secondAction)
                        .extracting("requirements.size")
                        .contains(1, 1);
            }

            @Test
            @DisplayName("should discard requirements that come after the last action")
            void shouldDiscardRequirementsAfterLastAction() {

                ActionContext<?, ?> firstAction = action();
                ActionContext<?, ?> secondAction = action();
                contexts.addAll(List.of(
                        requirement(),
                        requirement(),
                        firstAction,
                        requirement(),
                        secondAction,
                        requirement(),
                        requirement()
                ));

                assertThat(parser.sortAndCombineArtContexts(contexts))
                        .hasSize(2)
                        .containsExactly(firstAction, secondAction);
            }

            @Test
            @DisplayName("should only add direct preceding requirements to action")
            void shouldOnlyAddRelevantRequirementsToAction() {
                ActionContext<?, ?> firstAction = action();
                ActionContext<?, ?> secondAction = action();
                contexts.addAll(List.of(
                        requirement(),
                        requirement(),
                        requirement(),
                        firstAction,
                        requirement(),
                        secondAction
                ));

                assertThat(parser.sortAndCombineArtContexts(contexts))
                        .hasSize(2)
                        .extracting("requirements.size")
                        .contains(3, 1);
            }

            @Test
            @DisplayName("should only add nested actions to preceding action")
            void shouldAddActionsToCorrespondingAction() {
                ActionContext<?, ?> firstAction = action();
                ActionContext<?, ?> secondAction = action();
                contexts.addAll(List.of(
                        requirement(),
                        firstAction,
                        action(),
                        action(),
                        requirement(),
                        secondAction,
                        action()
                ));

                assertThat(parser.sortAndCombineArtContexts(contexts))
                        .hasSize(2)
                        .extracting("childActions.size")
                        .contains(2, 1);
            }

            @Test
            @DisplayName("should add single action to result")
            void shouldAddSingleAction() {

                ActionContext<?, ?> action = action();
                contexts.add(action);

                assertThat(parser.sortAndCombineArtContexts(contexts))
                        .hasSize(1)
                        .containsExactly(action);
            }
        }

        @Nested
        @DisplayName("with only requirements")
        class requirements {

            @Test
            @DisplayName("should add all requirements as flat list")
            void shouldAddAllRequirements() {

                List<RequirementContext<?, ?>> requirements = List.of(
                        requirement(),
                        requirement(),
                        requirement(),
                        requirement()
                );
                contexts.addAll(requirements);

                assertThat(parser.sortAndCombineArtContexts(contexts))
                        .hasSize(4)
                        .isEqualTo(requirements);
            }

            @Test
            @DisplayName("should add single requirement to result")
            void shouldAddASingleRequirement() {

                RequirementContext<?, ?> requirement = requirement();
                contexts.add(requirement);

                assertThat(parser.sortAndCombineArtContexts(contexts))
                        .hasSize(1)
                        .containsExactly(requirement);
            }
        }
    }
}