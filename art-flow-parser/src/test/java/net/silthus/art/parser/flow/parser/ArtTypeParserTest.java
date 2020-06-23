package net.silthus.art.parser.flow.parser;

import lombok.Data;
import lombok.SneakyThrows;
import net.silthus.art.ActionContext;
import net.silthus.art.api.actions.ActionConfig;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.annotations.Position;
import net.silthus.art.util.ConfigUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ArtTypeParser")
class ArtTypeParserTest {

    private ActionParser parser;
    private ActionManager actionManager;
    private ActionFactory<?, TestConfig> factory;

    @BeforeEach
    @SneakyThrows
    void beforeEach() {
        this.factory = mock(ActionFactory.class);
        this.actionManager = mock(ActionManager.class);

        when(actionManager.getFactory(anyString())).thenReturn(Optional.of(factory));

        when(factory.create(any())).thenAnswer(invocation -> new ActionContext<>(null, null, invocation.getArgument(0)));
        when(factory.getConfigClass()).thenReturn(Optional.of(TestConfig.class));
        when(factory.getConfigInformation()).thenReturn(ConfigUtil.getConfigFields(TestConfig.class));

        this.parser = new ActionParser(actionManager);
    }

    @Nested
    @DisplayName("parse()")
    class parse {

        @Test
        @SneakyThrows
        @DisplayName("should parse action config inside [...]")
        void shouldParseActionConfig() {

            assertThat(parser.accept("!foobar[delay=10s, cooldown:5s]")).isTrue();
            assertThat(parser.parse())
                    .extracting(actionContext -> actionContext.getOptions().getCooldown(), actionContext -> actionContext.getOptions().getDelay())
                    .contains(5000L, 10000L);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse action config inside (...)")
        void shouldParseActionConfigInBrackets() {

            assertThat(parser.accept("!foobar(delay=10s, cooldown:5s)")).isTrue();
            assertThat(parser.parse())
                    .extracting(actionContext -> actionContext.getOptions().getCooldown(), actionContext -> actionContext.getOptions().getDelay())
                    .contains(5000L, 10000L);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse art object config")
        void shouldParseCustomConfig() {

            assertThat(parser.accept("!foobar foo number=2")).isTrue();
            assertThat(parser.parse().getConfig())
                    .isNotEmpty().get()
                    .extracting("name", "number")
                    .contains("foo", 2);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse art object config and options")
        void shouldParseCustomConfigAndOptions() {

            assertThat(parser.accept("!foobar(delay=10) name=foo;number:2")).isTrue();
            ActionContext<?, ?> result = parser.parse();
            assertThat(result.getOptions())
                    .extracting(ActionConfig::getDelay)
                    .isEqualTo(500L);
            assertThat(result.getConfig())
                    .isNotEmpty().get()
                    .extracting("name", "number")
                    .contains("foo", 2);
        }
    }

    @Data
    static class TestConfig {
        @Position(0)
        private String name;
        private int number;
    }
}