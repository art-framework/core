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

import lombok.Data;
import lombok.SneakyThrows;
import net.silthus.art.*;
import net.silthus.art.conf.ActionConfig;
import net.silthus.art.util.ConfigUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "unused"})
class ArtObjectContextParserTest {

    private Configuration configuration;
    private ActionParser parser;
    private ActionFactory<?> factory;
    private ArtInformation artInformation;

    @BeforeEach
    @SneakyThrows
    void beforeEach() {
        configuration = mock(Configuration.class);
        ActionProvider actionProvider = mock(ActionProvider.class);
        when(configuration.actions()).thenReturn(actionProvider);
        factory = mock(ActionFactory.class);
        artInformation = mock(ArtInformation.class);
        when(factory.info()).thenReturn(artInformation);
        when(artInformation.getConfigMap()).thenReturn(ConfigUtil.getConfigFields(TestConfig.class));
        when(actionProvider.get(anyString())).thenAnswer(invocation -> Optional.of(factory));
        this.parser = new ActionParser(configuration);
    }

    private TestConfig extractConfig() {
        ArgumentCaptor<Map<ConfigMapType, ConfigMap>> argument = ArgumentCaptor.forClass(Map.class);
        verify(factory).create(argument.capture());

        TestConfig config = new TestConfig();

        assertThat(argument.getValue())
                .containsKey(ConfigMapType.SPECIFIC_ART_CONFIG);

        argument.getValue().get(ConfigMapType.SPECIFIC_ART_CONFIG).applyTo(config);

        return config;
    }

    @Test
    @DisplayName("should return false if accept(String) is null")
    void shouldNotAcceptNullStrings() {

        assertThat(parser.accept(null)).isFalse();
    }

    @Test
    @DisplayName("should return false if accept(String) is empty")
    void shouldNotAcceptEmptyStrings() {

        assertThat(parser.accept("  ")).isFalse();
    }

    @Nested
    @DisplayName("parse()")
    class parse {

        @Test
        @SneakyThrows
        @DisplayName("should parse action config inside [...]")
        void shouldParseActionConfig() {

            assertThat(parser.accept("!foobar[cooldown:5s, delay=10s]")).isTrue();
            assertThat(parser.parse())
                    .asInstanceOf(type(ActionContext.class))
                    .extracting(actionContext -> actionContext.getConfig().getCooldown(), actionContext -> actionContext.getConfig().getDelay())
                    .contains(5000L, 10000L);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse action config inside (...)")
        void shouldParseActionConfigInBrackets() {

            assertThat(parser.accept("!foobar(delay=10s, cooldown:5s)")).isTrue();
            assertThat(parser.parse())
                    .asInstanceOf(type(ActionContext.class))
                    .extracting(actionContext -> actionContext.getConfig().getCooldown(), actionContext -> actionContext.getConfig().getDelay())
                    .contains(5000L, 10000L);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse art object config")
        void shouldParseCustomConfig() {

            assertThat(parser.accept("!foobar foo number=2")).isTrue();

            parser.parse();

            TestConfig config = extractConfig();

            assertThat(config)
                    .extracting(TestConfig::getName, TestConfig::getNumber)
                    .contains("foo", 2);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse art object config and options")
        void shouldParseCustomConfigAndOptions() {

            assertThat(parser.accept("!foobar(delay=10) name=foo;number:2")).isTrue();

            assertThat(parser.parse())
                    .asInstanceOf(type(ActionContext.class))
                    .extracting(ActionContext::getConfig)
                    .extracting(ActionConfig::getDelay)
                    .isEqualTo(10L);

            assertThat(extractConfig())
                    .extracting(TestConfig::getName, TestConfig::getNumber)
                    .contains("foo", 2);
        }

        @Test
        @SneakyThrows
        @DisplayName("should throw if no matching factory is found")
        void shouldThrowIfNoIdentifierMatches() {

            when(configuration.actions().get(anyString())).thenReturn(Optional.empty());

            assertThat(parser.accept("!foobar")).isTrue();
            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(() -> parser.parse())
                    .withMessageContaining("No action with identifier \"foobar\" found");
        }
    }

    @Data
    static class TestConfig {
        @ConfigOption(position = 0)
        private String name;
        private int number;
    }

    static class WrongConfigClass {
        private final String foobar;

        WrongConfigClass(String foobar) {
            this.foobar = foobar;
        }
    }
}