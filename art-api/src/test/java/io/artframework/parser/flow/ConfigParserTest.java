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

package io.artframework.parser.flow;

import io.artframework.ArtParseException;
import io.artframework.ConfigMap;
import io.artframework.Configuration;
import io.artframework.annotations.ConfigOption;
import io.artframework.util.ConfigUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("ConfigParser")
class ConfigParserTest {

    private ConfigParser parser;

    @BeforeEach
    @SneakyThrows
    void beforeEach() {
        this.parser = parser(TestConfig.class);
    }

    @SneakyThrows
    private ConfigParser parser(Class<?> configClass) {
        return new ConfigParser(mock(Configuration.class), ConfigMap.of(ConfigMapType.ART_CONFIG, ConfigUtil.getConfigFields(configClass)));
    }

    @Nested
    @DisplayName("parse()")
    class parse {

        // TODO: more tests

        @Test
        @SneakyThrows
        @DisplayName("should parse single config setting without position annotation")
        void shouldParseConfigWithSingleField() {

            ConfigParser parser = parser(SingleFieldConfig.class);

            parser.accept("10");
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new SingleFieldConfig()))
                    .extracting(SingleFieldConfig::getAmount)
                    .isEqualTo(10.0);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse config with key=value parameters")
        void shouldParseConfigWithKeyValueParameter() {

            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("name=foobar, required=true")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName, TestConfig::isRequired)
                    .contains("foobar", true);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse config with key:value parameters")
        void shouldParseConfigWithKeyValueParameterWithColons() {

            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("name:foobar, required:true")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName, TestConfig::isRequired)
                    .contains("foobar", true);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse config with key:value,key=value parameters without space")
        void shouldParseConfigWithKeyValueParameterWithoutSpace() {

            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("name:foobar,required=true")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName, TestConfig::isRequired)
                    .contains("foobar", true);
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse config with key:value; key=value parameters with semicolon")
        void shouldParseConfigWithKeyValueParameterWithSemicolon() {

            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("name:foobar; required=true")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName, TestConfig::isRequired)
                    .contains("foobar", true);
        }

        @Test
        @SneakyThrows
        @DisplayName("should throw if positioned parameters do not come first: key=value, value, value")
        void shouldThrowIfPositionedParameterIsNotFirst() {

            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("name:foobar, true, barfoo")).isTrue();
            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(parser::parse)
                    .withMessageContaining("Positioned parameters must come first");
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse config with key=value and position parameters")
        void shouldParseConfigWithKeyValueAndPositionParameters() {
            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("foobar, optional=barfoo")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName, TestConfig::getOptional)
                    .contains("foobar", "barfoo");
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse value of string inside \"foobar with spaces\"")
        void shouldParseValueOfString() {
            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("\"foobar with spaces\"")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName)
                    .isEqualTo("foobar with spaces");
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse value of string with key inside name=\"foobar with spaces\"")
        void shouldParseValueOfStringWithKey() {
            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("\"foobar with spaces\"")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName)
                    .isEqualTo("foobar with spaces");
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse values with spaces foobar true spaces")
        void shouldParseValuesWithSpaces() {
            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("foobar true spaces")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName, TestConfig::isRequired, TestConfig::getOptional)
                    .contains("foobar", true, "spaces");
        }

        @Test
        @SneakyThrows
        @DisplayName("should parse values with quotes in between spaces: foobar optional=\"with spaces\"")
        void shouldParseQuotedValueInBetween() {
            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("foobar optional=\"with spaces\"")).isTrue();
            ConfigMap result = parser.parse();
            assertThat(result.applyTo(new TestConfig()))
                    .extracting(TestConfig::getName, TestConfig::getOptional)
                    .contains("foobar", "with spaces");
        }

        @Test
        @SneakyThrows
        @DisplayName("should throw if config needs explicit parameters and does not define positions")
        void shouldThrowIfExplicitParamsAreNeeded() {
            ConfigParser parser = parser(ConfigWithoutPositions.class);

            assertThat(parser.accept("foobar spaces")).isTrue();
            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(parser::parse)
                    .withMessageContaining("Config does not define positioned parameters");
        }

        @Test
        @SneakyThrows
        @DisplayName("should throw if required parameter is missing")
        void shouldThrowIfRequiredParamIsMissing() {
            ConfigParser parser = parser(ConfigWithRequired.class);

            assertThat(parser.accept("foobar")).isTrue();
            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(parser::parse)
                    .withMessageContaining("required");
        }

        @Test
        @SneakyThrows
        @DisplayName("should throw if value is missing: name=")
        void shouldThrowIfValueIsMissing() {
            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("name=")).isTrue();
            assertThatExceptionOfType(ArtParseException.class)
                    .isThrownBy(parser::parse)
                    .withMessageContaining("empty value");
        }

        @Test
        @SneakyThrows
        @DisplayName("should not throw if value is missing between quotes: name=\"\"")
        void shouldNotThrowIfValueIsMissingBetweenQuotes() {
            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("name=\"\"")).isTrue();
            assertThatCode(() -> {
                TestConfig result = parser.parse().applyTo(new TestConfig());
                assertThat(result).extracting(TestConfig::getName)
                        .isEqualTo("");
            }).doesNotThrowAnyException();
        }
    }

    @Data
    public static class TestConfig {

        @ConfigOption(position = 0)
        private String name;
        @ConfigOption(position = 1)
        private boolean required = false;
        @ConfigOption(position = 2)
        private String optional;
    }

    @Data
    public static class SingleFieldConfig {
        @ConfigOption(required = true)
        private double amount;
    }

    @Data
    public static class ConfigWithoutPositions {

        @ConfigOption
        private String name;
        @ConfigOption
        private String optional;
    }

    @Data
    public static class ConfigWithRequired {

        @ConfigOption(position = 0)
        private String name;
        @ConfigOption(required = true)
        private String required;
        @ConfigOption
        private String optional;
    }

}