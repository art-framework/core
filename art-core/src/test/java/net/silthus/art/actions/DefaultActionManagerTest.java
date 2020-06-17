package net.silthus.art.actions;

import lombok.SneakyThrows;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionConfig;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.parser.ARTParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("DefaultActionManager")
class DefaultActionManagerTest {

    private DefaultActionManager actionManager;

    @BeforeEach
    void beforeEach() {
        this.actionManager = new DefaultActionManager(new HashSet<>());
        actionManager.setLogger(Logger.getGlobal());
    }

    @Nested
    @DisplayName("exists(String)")
    class exists {

        @Test
        @DisplayName("should return false if action is not registered")
        void shouldNotExist() {

            assertThat(actionManager.exists("foobar"))
                    .isFalse();
        }

        @Test
        @DisplayName("should return true if action was registered")
        void shouldExist() {
            actionManager.getActionFactories().put("foobar", mock(ActionFactory.class));

            assertThat(actionManager.exists("foobar"))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("register(...)")
    class register {

        @Test
        @DisplayName("should store registered actions in memory")
        void shouldAddActionsToMemory() {

            actionManager.register(Map.of("foobar", mock(ActionFactory.class)));

            assertThat(actionManager.exists("foobar")).isTrue();
        }

        @Test
        @DisplayName("should not register actions with a duplicate identifier")
        void shouldNotAddDuplicateActions() {

            ActionFactory mock = mock(ActionFactory.class);
            when(mock.getIdentifier()).thenReturn("foobar1");
            when(mock.getArtObject()).thenReturn(mock(Action.class));
            actionManager.register(Map.of("foobar", mock));

            assertThat(actionManager.getActionFactories().get("foobar"))
                    .isNotNull()
                    .extracting("identifier")
                    .isEqualTo("foobar1");

            ActionFactory mock2 = mock(ActionFactory.class);
            when(mock2.getIdentifier()).thenReturn("foobar2");
            when(mock2.getArtObject()).thenReturn(mock(Action.class));
            actionManager.register(Map.of("foobar", mock2));

            assertThat(actionManager.getActionFactories().get("foobar"))
                    .isNotNull()
                    .extracting("identifier")
                    .isNotEqualTo("foobar2");
        }

        @Test
        @DisplayName("should register all actions inside the map")
        void shouldAddAllActionsInTheMap() {

            actionManager.register(Map.of(
                    "test1", mock(ActionFactory.class),
                    "test2", mock(ActionFactory.class),
                    "test3", mock(ActionFactory.class),
                    "test4", mock(ActionFactory.class),
                    "test5", mock(ActionFactory.class)
            ));

            assertThat(actionManager.getActionFactories())
                    .hasSize(5)
                    .containsKeys("test1", "test2","test3","test4","test5");
        }
    }

    @Nested
    @DisplayName("create(ARTConfig)")
    class create {

        private ARTConfig config;

        @BeforeEach
        void beforeEach() {
            config = new ARTConfig();
        }

        @Test
        @DisplayName("should return an empty list of multiple parsers match")
        void shouldReturnEmptyListIfMultipleParsersMatch() {

            ARTParser parser1 = mock(ARTParser.class);
            when(parser1.matches(config)).thenReturn(true);
            ARTParser parser2 = mock(ARTParser.class);
            when(parser2.matches(config)).thenReturn(true);

            actionManager.getParser().add(parser1);
            actionManager.getParser().add(parser2);

            assertThat(actionManager.create(config))
                    .isEmpty();
        }

        @Test
        @DisplayName("should return an empty list if no parsers match")
        void shouldReturnEmptyListIfNoParserMatches() {

            ARTParser parser1 = mock(ARTParser.class);
            when(parser1.matches(config)).thenReturn(false);
            ARTParser parser2 = mock(ARTParser.class);
            when(parser2.matches(config)).thenReturn(false);

            actionManager.getParser().add(parser1);
            actionManager.getParser().add(parser2);

            assertThat(actionManager.create(config))
                    .isEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse the config if exactly one parser matches")
        void shouldParseTheConfigIfExactlyOneParserMatches() {

            ARTParser parser = mock(ARTParser.class);
            when(parser.matches(config)).thenReturn(true);

            actionManager.getParser().add(parser);

            actionManager.create(config);

            verify(parser, times(1)).parseActions(config);
        }
    }


    @Nested
    @DisplayName("create(...) with TypeFilter")
    class createWithType {

        @Test
        @DisplayName("should filter out actions without matching types")
        void shouldFilterActions() {

            DefaultActionManager actionManager = mock(DefaultActionManager.class);
            when(actionManager.create(any(), any())).thenCallRealMethod();
            when(actionManager.create(any())).thenReturn(List.of(
                    new ActionContext<>(String.class, (s, context) -> {}, new ActionConfig<>()),
                    new ActionContext<>(Integer.class, (s, context) -> {}, new ActionConfig<>()),
                    new ActionContext<>(String.class, (s, context) -> {}, new ActionConfig<>())
            ));

            assertThat(actionManager.create(String.class, new ARTConfig()))
                    .hasSize(2);
        }
    }

    @Nested
    @DisplayName("getFactory(...)")
    class getFactory {

        @Test
        @DisplayName("should return wrapped optional")
        void shouldReturnWrappedOptional() {

            actionManager.getActionFactories().put("foobar", mock(ActionFactory.class));

            assertThat(actionManager.getFactory("foobar"))
                    .isNotEmpty();
        }

        @Test
        @DisplayName("should return empty optional if action factory does not exist")
        void shouldReturnEmptyOptional() {

            assertThat(actionManager.getFactory("foobar"))
                    .isEmpty();
        }
    }
}