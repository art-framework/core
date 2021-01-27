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

package io.artframework.parser;

import io.artframework.ART;
import io.artframework.ConfigMap;
import io.artframework.ParseException;
import io.artframework.ResolveContext;
import io.artframework.ResolveException;
import io.artframework.Resolver;
import io.artframework.annotations.ConfigOption;
import io.artframework.annotations.Resolve;
import io.artframework.util.ConfigUtil;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SuppressWarnings("ALL")
@DisplayName("ConfigParser")
class ConfigParserTest {

    @SneakyThrows
    private ConfigParser parser(Class<?> configClass) {
        return new ConfigParser(ConfigMap.of(ConfigUtil.getConfigFields(configClass)));
    }

    @Nested
    @DisplayName("parse()")
    class parse {

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
            assertThatExceptionOfType(ParseException.class)
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
            assertThatExceptionOfType(ParseException.class)
                    .isThrownBy(parser::parse)
                    .withMessageContaining("Config does not define positioned parameters");
        }

        @Test
        @SneakyThrows
        @DisplayName("should throw if required parameter is missing")
        void shouldThrowIfRequiredParamIsMissing() {
            ConfigParser parser = parser(ConfigWithRequired.class);

            assertThat(parser.accept("foobar")).isTrue();
            assertThatExceptionOfType(ParseException.class)
                    .isThrownBy(parser::parse)
                    .withMessageContaining("required");
        }

        @Test
        @SneakyThrows
        @DisplayName("should not throw if value is missing: name=")
        void shouldThrowIfValueIsMissing() {
            ConfigParser parser = parser(TestConfig.class);

            assertThat(parser.accept("name=")).isTrue();
            assertThatCode(() -> {
                TestConfig result = parser.parse().applyTo(new TestConfig());
                assertThat(result).extracting(TestConfig::getName)
                        .isEqualTo("");
            }).doesNotThrowAnyException();
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

        @Test
        @DisplayName("should parse array with explicit key=[...] syntax")
        void shouldParseArrayWithExplicitSyntax() {

            ConfigParser parser = parser(ConfigWithArray.class);

            assertThat(parser.accept("messages=[my cool, multiline, message with spaces]")).isTrue();
            assertThatCode(() -> {
                ConfigWithArray result = parser.parse().applyTo(new ConfigWithArray());
                assertThat(result.getMessages())
                        .contains("my cool", "multiline", "message with spaces");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should parse array with explicit [...] value only syntax")
        void shouldParseArrayWithExplicitValueOnlySyntax() {

            ConfigParser parser = parser(ConfigWithArray.class);

            assertThat(parser.accept("[my cool, multiline, message with spaces]")).isTrue();
            assertThatCode(() -> {
                ConfigWithArray result = parser.parse().applyTo(new ConfigWithArray());
                assertThat(result.getMessages())
                        .contains("my cool", "multiline", "message with spaces");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should auto parse arrays if they are the only argument")
        void shouldAutoParseArrayIfOnlyArgument() {
            ConfigParser parser = parser(ConfigWithArray.class);

            assertThat(parser.accept("my cool, multiline, message with spaces")).isTrue();
            assertThatCode(() -> {
                ConfigWithArray result = parser.parse().applyTo(new ConfigWithArray());
                assertThat(result.getMessages())
                        .contains("my cool", "multiline", "message with spaces");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should parse array with quotes as strings with commas")
        void shouldParseArrayWithQuotes() {

            ConfigParser parser = parser(ConfigWithArray.class);

            assertThat(parser.accept("\"my cool, multiline, message with spaces\", and mixed, commas")).isTrue();
            assertThatCode(() -> {
                ConfigWithArray result = parser.parse().applyTo(new ConfigWithArray());
                assertThat(result.getMessages())
                        .contains("my cool, multiline, message with spaces", "and mixed", "commas");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should parse explicit array sytnax with quotes as strings with commas")
        void shouldParseArrayWithQuotesExplicitSyntax() {

            ConfigParser parser = parser(ConfigWithArray.class);

            assertThat(parser.accept("[\"my cool, multiline, message with spaces\", and mixed, commas]")).isTrue();
            assertThatCode(() -> {
                ConfigWithArray result = parser.parse().applyTo(new ConfigWithArray());
                assertThat(result.getMessages())
                        .contains("my cool, multiline, message with spaces", "and mixed", "commas");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should parse single element array that contains spaces")
        void shouldParseSingleLineArrayWithSpaces() {
            ConfigParser parser = parser(ConfigWithArray.class);

            assertThat(parser.accept("my cool array with spaces")).isTrue();
            assertThatCode(() -> {
                ConfigWithArray result = parser.parse().applyTo(new ConfigWithArray());
                assertThat(result.getMessages())
                        .contains("my cool array with spaces");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should parse location config with negative values")
        void shouldParseLocationConfigWithNegativeValues() {

            ConfigParser parser = parser(LocationConfig.class);

            assertThat(parser.accept("0,45,-102")).isTrue();
            assertThatCode(() -> assertThat(parser.parse()
                    .applyTo(new LocationConfig()))
                    .extracting(LocationConfig::getX, LocationConfig::getY, LocationConfig::getZ)
                    .contains(0, 45, -102)
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should set resolvable value to null if config field is optional")
        void shouldSetResolvableValueToNullIfOptional() throws ParseException {

            ConfigParser parser = parser(ResolverConfig.class);

            assertThat(parser.accept("location=$(1, 2, 3)")).isTrue();
            assertThat(parser.parse().resolve(ART.globalScope()).applyTo(new ResolverConfig()))
                    .extracting(ResolverConfig::getLocation)
                    .isEqualTo(null);
        }

        @Nested
        class ResolverTests {

            @BeforeEach
            void setUp() {

                ART.globalScope().configuration()
                        .resolvers().add(LocationConfig.class);
            }

            @Test
            @DisplayName("should parse resolver key values as own key value pairs")
            void shouldParseResolverKeyValuesAsOwnKeyValuePairs() throws ParseException {

                ConfigParser parser = parser(ResolverConfig.class);

                assertThat(parser.accept("location=$(1, 2, 3)")).isTrue();
                assertThat(parser.parse().resolve(ART.globalScope()).applyTo(new ResolverConfig()))
                        .extracting(ResolverConfig::getLocation)
                        .extracting(LocationConfig::getX, LocationConfig::getY, LocationConfig::getZ)
                        .contains(1, 2, 3);
            }

            @Test
            @DisplayName("should parse nested resolvable key vlaues into resolver")
            void shouldImplicitlyParseOneRootArgumentToNestedKeyValueList() throws ParseException {

                ConfigParser parser = parser(ResolverConfig.class);

                assertThat(parser.accept("1, 2, 3")).isTrue();
                assertThat(parser.parse().resolve(ART.globalScope()).applyTo(new ResolverConfig()))
                        .extracting(ResolverConfig::getLocation)
                        .extracting(LocationConfig::getX, LocationConfig::getY, LocationConfig::getZ)
                        .contains(1, 2, 3);
            }

            @Test
            @DisplayName("should parse nested resolver argument key values into resolver")
            void shouldParseNestedResolverArguments() throws ParseException {

                ConfigParser parser = parser(ResolverConfig.class);

                assertThat(parser.accept("$(1, 2, 3)")).isTrue();
                assertThat(parser.parse().resolve(ART.globalScope()).applyTo(new ResolverConfig()))
                        .extracting(ResolverConfig::getLocation)
                        .extracting(LocationConfig::getX, LocationConfig::getY, LocationConfig::getZ)
                        .contains(1, 2, 3);
            }

            @Test
            @DisplayName("should keep defaults if resolved value is null")
            void shouldKeepDefaultsIfResolvedValueIsNull() throws ParseException {

                ConfigParser parser = parser(ResolverExtraConfig.class);

                ResolverExtraConfig config = new ResolverExtraConfig();
                config.location = new LocationConfig(10, 20, 30);

                assertThat(parser.accept("message=foobar")).isTrue();
                
                ResolverExtraConfig actual = parser.parse().resolve(ART.globalScope()).applyTo(config);

                assertThat(actual)
                        .extracting(resolverConfig -> resolverConfig.getLocation())
                        .isNotNull()
                        .extracting(LocationConfig::getX, LocationConfig::getY, LocationConfig::getZ)
                        .contains(10, 20, 30);
                assertThat(actual)
                        .extracting(ResolverExtraConfig::getMessage)
                        .isEqualTo("foobar");
            }
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

    @Data
    @ConfigOption
    public static class ConfigWithArray {

        private String[] messages;
    }

    @Data
    @ConfigOption
    public static class ConfigWithVargArray {

        @ConfigOption
        private int myVal = 5;
        @ConfigOption
        private String[] messages;
    }

    @Data
    @ConfigOption
    public static class LocationConfig implements Resolver<LocationConfig> {

        @ConfigOption(position = 0)
        private int x;
        @ConfigOption(position = 1)
        private int y;
        @ConfigOption(position = 2)
        private int z;

        public LocationConfig() {
        }

        public LocationConfig(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public LocationConfig resolve(ResolveContext context) throws ResolveException {

            return new LocationConfig(x, y, z);
        }
    }

    @Data
    public static class ResolverConfig {

        @Resolve
        private LocationConfig location;
    }

    @Getter
    public static class ResolverExtraConfig {

        @Resolve
        private LocationConfig location;
        @ConfigOption
        private String message;
    }
}