package net.silthus.art.parser.flow;

import net.silthus.art.api.actions.*;
import net.silthus.art.api.config.ARTObjectConfig;
import net.silthus.art.api.parser.ARTParseException;
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
        @DisplayName("should return false if not instance of string")
        public void shouldReturnFalseIfInstanceDoesNotMatch() {
            assertThat(parser.matches(new ARTObjectConfig<>()))
                    .isFalse();
        }

        @Test
        @DisplayName("should return true if instance of string")
        public void shouldReturnTrueIfString() {
            assertThat(parser.matches("foobar"))
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
                    .isThrownBy(() -> parser.next(null));
        }

        @Test
        @DisplayName("should throw if config object is of invalid type")
        public void shouldThrowIfInvalidType() {
            assertThatExceptionOfType(ARTParseException.class)
                    .isThrownBy(() -> parser.next(new ARTObjectConfig<>()));
        }

        @Nested
        @DisplayName("action parser")
        class actions {

            @Test
            @DisplayName("should return action context")
            public void shouldReturnAction() {

                ActionFactory<String, String> foobarFactory = mock(ActionFactory.class);
                when(actionManager.getFactory("foobar"))
                        .thenReturn(Optional.of(foobarFactory));

                when(foobarFactory.create(any()))
                        .thenReturn(new ActionContext<>(String.class, (s, context) -> {}, new ActionConfig<>()));

                assertThatCode(() -> {
                    assertThat(parser.next("!foobar"))
                            .isNotNull()
                            .isInstanceOf(ActionContext.class);
                }).doesNotThrowAnyException();
            }
        }
    }
}