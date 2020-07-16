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

import lombok.SneakyThrows;
import net.silthus.art.conf.ActionConfig;
import net.silthus.art.conf.ConfigFieldInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

@DisplayName("ActionFactory")
public class ArtFactoryTest {

    private ArtFactory<?, ?> factory;

    @BeforeEach
    public void beforeEach() {
        this.factory = factory(String.class, new TestAction());
    }

    public static <TTarget, TConfig> ArtFactory<?, ?> factory(Class<TTarget> targetClass) {
        return factory(targetClass, (target, context) -> {
        });
    }

    public static <TTarget, TConfig> ArtFactory<TTarget, TConfig, Action<TTarget, TConfig>, ActionConfig<TConfig>> factory(Class<TTarget> targetClass, Action<TTarget, TConfig> action) {
        return new ArtFactory<TTarget, TConfig, Action<TTarget, TConfig>, ActionConfig<TConfig>>(mock(Storage.class), targetClass, action) {

            @Override
            public AbstractArtObjectContext<TTarget, TConfig, ActionConfig<TConfig>> create(ActionConfig<TConfig> config) {
                return null;
            }
        };
    }

    @Nested
    @DisplayName("getConfigString()")
    class getConfigString {

        @Test
        @DisplayName("should return an empty string if no config fields exist")
        void shouldReturnEmptyStringIfConfigIsEmpty() {

            assertThat(factory.getConfigString()).isEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return sorted and formatted config string")
        void shouldFormattedAndStoredString() {

            factory.initialize();
            assertThat(factory.getConfigString())
                    .isEqualTo("parent_field=foobar, required*=0, all_annotations*=2.0, default_field=world, nested.nested_field=foobar, no_annotations=false");
        }
    }


    @Nested
    @DisplayName("initialize()")
    public class Initialize {

        @BeforeEach
        public void beforeEach() {

            assertThat(factory.getIdentifier()).isNullOrEmpty();
            assertThat(factory.getConfigClass()).isEmpty();
        }

        @Test
        @DisplayName("should use annotations")
        public void shouldUseAnnotations() {

            assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            assertThat(factory.getIdentifier()).isEqualTo("Test");
            assertThat(factory.getConfigClass()).contains(TestConfig.class);
        }

        @Test
        @DisplayName("should use description annotation on class")
        public void shouldUseDescriptionAnnotation() {

            assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            assertThat(factory.getDescription()).isEqualTo(new String[]{"Description"});
        }

        @Test
        @DisplayName("should not override manually set name and config information")
        public void shouldNotOverrideManualSetters() {

            factory.setIdentifier("foo");
            factory.setConfigClass(null);

            assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigClass()).contains(TestConfig.class);
        }

        @Test
        @DisplayName("should throw ActionRegistrationException if missing annotations")
        public void shouldThrowIfMissingAnnotations() {

            factory = factory(String.class);

            assertThatExceptionOfType(ArtObjectInformationException.class)
                    .isThrownBy(() -> factory.initialize());
        }

        @Test
        @DisplayName("should not throw if manual set but has missing annotations")
        public void shouldNotThrowIfNoAnnotationButManualInfo() {

            factory.setIdentifier("foo");
            factory.setConfigClass(TestConfig.class);

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigClass()).contains(TestConfig.class);
        }

        @Test
        @DisplayName("should not throw if missing config information")
        public void shouldNotThrowIfMissingConfigInformation() {

            factory = factory(String.class);
            factory.setIdentifier("foobar");

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier())
                    .isEqualTo("foobar");
            assertThat(factory.getConfigClass())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("should use annotations on method")
        public void shouldUseMethodAnnotation() {

            factory = factory(String.class, new Action<String, TestConfig>() {
                @ArtOptions(value = "foo", config = TestConfig.class)
                @Override
                public void execute(Target<String> s, ActionContext<String> context) {
                }
            });

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigClass()).contains(TestConfig.class);
        }

        @SuppressWarnings("unchecked")
        @Nested
        @DisplayName("creates ConfigFieldInformation that")
        class ConfigAnnotations {

            @Test
            @DisplayName("should load all fields including superclass")
            public void shouldLoadAllFields() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation())
                        .hasSizeGreaterThanOrEqualTo(5)
                        .containsKeys(
                                "parent_field",
                                "no_annotations",
                                "required",
                                "default_field",
                                "all_annotations"
                        );
            }

            @Test
            @DisplayName("should load required annotation")
            public void shouldLoadRequiredAttribute() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("required"))
                        .extracting(ConfigFieldInformation::isRequired)
                        .isEqualTo(true);
            }

            @Test
            @DisplayName("should load description annotation")
            public void shouldLoadDescriptionAttribute() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("default_field"))
                        .extracting(ConfigFieldInformation::getDescription)
                        .isEqualTo(new String[]{"World to teleport the player to."});
            }

            @Test
            @DisplayName("should load default value")
            public void shouldLoadDefaultValue() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("default_field"))
                        .extracting(ConfigFieldInformation::getDefaultValue)
                        .isEqualTo("world");
            }

            @Test
            @DisplayName("should load required field with default value")
            public void shouldLoadRequiredDefaultValue() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("all_annotations"))
                        .extracting(ConfigFieldInformation::getDefaultValue, ConfigFieldInformation::getDescription)
                        .contains(2.0d, new String[]{"Required field with default value."});
            }

            @Test
            @DisplayName("should load nested config objects")
            public void shouldLoadNestedObjects() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation())
                        .containsKeys("nested.nested_field");
                assertThat(factory.getConfigInformation().get("nested.nested_field"))
                        .extracting(ConfigFieldInformation::getDescription, ConfigFieldInformation::getDefaultValue)
                        .contains(new String[]{"nested config field"}, "foobar");
            }

            @Test
            @DisplayName("should not load nested object fields")
            public void shouldNotAddNestedBase() {
                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation())
                        .doesNotContainKey("nested");
            }

            @Test
            @DisplayName("should ignore @Ignored fields")
            public void shouldIgnoredIgnored() {
                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation())
                        .doesNotContainKey("ignored");
            }

            @Test
            @DisplayName("should load field position annotation")
            public void shouldLoadFieldPosition() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("required"))
                        .extracting(ConfigFieldInformation::getPosition)
                        .isEqualTo(1);
                assertThat(factory.getConfigInformation().get("parent_field"))
                        .extracting(ConfigFieldInformation::getPosition)
                        .isEqualTo(0);
            }

            @Test
            @DisplayName("should throw if same field position is found")
            public void shouldThrowExceptionForSamePosition() {

                ArtFactory<String, ErrorConfig, Action<String, ErrorConfig>, ActionConfig<ErrorConfig>> factory = factory(String.class, new Action<String, ErrorConfig>() {
                    @ArtOptions(value = "test", config = ErrorConfig.class)
                    @Override
                    public void execute(Target<String> s, ActionContext<String> context) {

                    }
                });

                assertThatExceptionOfType(ArtObjectInformationException.class)
                        .isThrownBy(factory::initialize)
                        .withMessageContaining("found same position");
            }
        }
    }

    @Nested
    @DisplayName("create(ActionConfig<TConfig>)")
    public class Create {

        @BeforeEach
        public void beforeEach() {

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
        }
    }

    @ArtOptions(
            value = "Test",
            description = "Description",
            config = TestConfig.class
    )
    public static class TestAction implements Action<String> {
        @Override
        public void execute(Target<String> target, ExecutionContext<ActionContext<String>> context) {

        }
    }

    public static class ConfigBase {

        @ConfigOption(position = 0)
        private final String parentField = "foobar";
    }

    public static class TestConfig extends ConfigBase {

        private boolean noAnnotations;
        @ConfigOption(required = true, position = 1)
        private int required;
        @ConfigOption(description = "World to teleport the player to.")
        private final String defaultField = "world";

        @ConfigOption(description = "Required field with default value.", required = true)
        private final double allAnnotations = 2.0d;

        @Ignore
        private final String ignored = "";

        private final NestedConfig nested = new NestedConfig();
    }

    public static class NestedConfig {
        @ConfigOption(description = "nested config field")
        private final String nestedField = "foobar";
    }

    public static class ErrorConfig extends ConfigBase {

        @ConfigOption(position = 0)
        private final int error = 2;
    }
}