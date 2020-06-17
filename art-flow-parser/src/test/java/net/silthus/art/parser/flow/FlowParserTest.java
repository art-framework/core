package net.silthus.art.parser.flow;

import net.silthus.art.api.actions.*;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.config.ARTObjectConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("FlowParser")
class FlowParserTest {

    private FlowParser parser;
    private ActionManager actionManager;

    @BeforeEach
    void beforeEach() {
        actionManager = mock(ActionManager.class);
        parser = new FlowParser(actionManager);
    }

    @Nested
    @DisplayName("matches(Object)")
    class matches {

        @Test
        @DisplayName("should return false if config is null")
        public void shouldReturnFalseIfNull() {
            assertThat(parser.matches(null))
                    .isFalse();
        }

        @Test
        @DisplayName("should return true if instance of string")
        public void shouldReturnTrueIfString() {
            ARTConfig config = new ARTConfig();
            config.getArt().add("!foobar");
            assertThat(parser.matches(config))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("next(Object)")
    class next {

        @Test
        @DisplayName("should throw if config object is null")
        public void shouldThrowIfObjectIsNull() {
            assertThatExceptionOfType(NullPointerException.class)
                    .isThrownBy(() -> parser.parseActions(null));
        }

        @Nested
        @DisplayName("action parser")
        class actions {

            @Test
            @DisplayName("should return action context")
            @SuppressWarnings("unchecked")
            public void shouldReturnAction() {

                ActionFactory<String, String> foobarFactory = mock(ActionFactory.class);
                when(actionManager.getFactory("foobar"))
                        .thenReturn(Optional.of(foobarFactory));

                Action<String, String> action = (s, context) -> {};
                when(foobarFactory.create(any()))
                        .thenReturn(new ActionContext<>(String.class, action, new ActionConfig<>()));

                ARTConfig config = new ARTConfig();
                config.getArt().add("!foobar");

                assertThatCode(() -> {
                    assertThat(parser.parseActions(config))
                            .hasSize(1)
                            .first()
                            .extracting(ActionContext::getAction)
                            .isEqualTo(action);
                }).doesNotThrowAnyException();
            }
        }
    }
}