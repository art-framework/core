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

package net.silthus.art.api.factory;

import net.silthus.art.ActionContext;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtObjectRegistrationException;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionConfig;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.annotations.*;
import net.silthus.art.api.config.ConfigFieldInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ActionFactory")
public class ArtFactoryTest {

    private ActionFactory<String, TestConfig> actionFactory;

    @BeforeEach
    public void beforeEach() {
        this.actionFactory = new ActionFactory<>(String.class, new TestAction());
    }

    @Nested
    @DisplayName("initialize()")
    public class Initialize {

        @BeforeEach
        public void beforeEach() {

            assertThat(actionFactory.getIdentifier()).isNullOrEmpty();
            assertThat(actionFactory.getConfigClass()).isEmpty();
        }

        @Test
        @DisplayName("should use annotations")
        public void shouldUseAnnotations() {

            assertThatCode(() -> actionFactory.initialize())
                    .doesNotThrowAnyException();

            assertThat(actionFactory.getIdentifier()).isEqualTo("Test");
            assertThat(actionFactory.getConfigClass()).contains(TestConfig.class);
        }

        @Test
        @DisplayName("should not override manually set name and config information")
        public void shouldNotOverrideManualSetters() {

            actionFactory.setIdentifier("foo");
            actionFactory.setConfigClass(null);

            assertThatCode(() -> actionFactory.initialize())
                    .doesNotThrowAnyException();

            assertThat(actionFactory.getIdentifier()).isEqualTo("foo");
            assertThat(actionFactory.getConfigClass()).contains(TestConfig.class);
        }

        @Test
        @DisplayName("should throw ActionRegistrationException if missing annotations")
        public void shouldThrowIfMissingAnnotations() {

            actionFactory = new ActionFactory<>(String.class, (s, context) -> {
            });

            assertThatExceptionOfType(ArtObjectRegistrationException.class)
                    .isThrownBy(() -> actionFactory.initialize());
        }

        @Test
        @DisplayName("should not throw if manual set but has missing annotations")
        public void shouldNotThrowIfNoAnnotationButManualInfo() {

            actionFactory = new ActionFactory<>(String.class, (s, context) -> {
            });
            actionFactory.setIdentifier("foo");
            actionFactory.setConfigClass(TestConfig.class);

            assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
            assertThat(actionFactory.getIdentifier()).isEqualTo("foo");
            assertThat(actionFactory.getConfigClass()).contains(TestConfig.class);
        }

        @Test
        @DisplayName("should not throw if missing config information")
        public void shouldNotThrowIfMissingConfigInformation() {

            actionFactory = new ActionFactory<>(String.class, (s, context) -> {
            });
            actionFactory.setIdentifier("foobar");

            assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
            assertThat(actionFactory.getIdentifier())
                    .isEqualTo("foobar");
            assertThat(actionFactory.getConfigClass())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("should use annotations on method")
        public void shouldUseMethodAnnotation() {

            actionFactory = new ActionFactory<>(String.class, new Action<>() {

                @Name("foo")
                @Config(TestConfig.class)
                @Override
                public void execute(String s, ActionContext<String, TestConfig> context) {

                }
            });

            assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
            assertThat(actionFactory.getIdentifier()).isEqualTo("foo");
            assertThat(actionFactory.getConfigClass()).contains(TestConfig.class);
        }

        @Nested
        @DisplayName("creates ConfigFieldInformation that")
        class ConfigAnnotations {

            @Test
            @DisplayName("should load all fields including superclass")
            public void shouldLoadAllFields() {

                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation())
                        .hasSizeGreaterThanOrEqualTo(5)
                        .containsKeys(
                                "parentField",
                                "noAnnotations",
                                "required",
                                "defaultField",
                                "allAnnotations"
                        );
            }

            @Test
            @DisplayName("should load required annotation")
            public void shouldLoadRequiredAttribute() {

                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation().get("required"))
                        .extracting(ConfigFieldInformation::isRequired)
                        .isEqualTo(true);
            }

            @Test
            @DisplayName("should load description annotation")
            public void shouldLoadDescriptionAttribute() {

                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation().get("defaultField"))
                        .extracting(ConfigFieldInformation::getDescription)
                        .isEqualTo(new String[] {"World to teleport the player to."});
            }

            @Test
            @DisplayName("should load default value")
            public void shouldLoadDefaultValue() {

                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation().get("defaultField"))
                        .extracting(ConfigFieldInformation::getDefaultValue)
                        .isEqualTo("world");
            }

            @Test
            @DisplayName("should load required field with default value")
            public void shouldLoadRequiredDefaultValue() {

                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation().get("allAnnotations"))
                        .extracting(ConfigFieldInformation::getDefaultValue, ConfigFieldInformation::getDescription)
                        .contains(2.0d, new String[] {"Required field with default value."});
            }

            @Test
            @DisplayName("should load nested config objects")
            public void shouldLoadNestedObjects() {

                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation())
                        .containsKeys("nested.nestedField");
                assertThat(actionFactory.getConfigInformation().get("nested.nestedField"))
                        .extracting(ConfigFieldInformation::getDescription, ConfigFieldInformation::getDefaultValue)
                        .contains(new String[] {"nested config field"}, "foobar");
            }

            @Test
            @DisplayName("should not load nested object fields")
            public void shouldNotAddNestedBase() {
                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation())
                        .doesNotContainKey("nested");
            }

            @Test
            @DisplayName("should ignore @Ignored fields")
            public void shouldIgnoredIgnored() {
                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation())
                        .doesNotContainKey("ignored");
            }

            @Test
            @DisplayName("should load field position annotation")
            public void shouldLoadFieldPosition() {

                assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
                assertThat(actionFactory.getConfigInformation().get("required"))
                        .extracting(ConfigFieldInformation::getPosition)
                        .isEqualTo(1);
                assertThat(actionFactory.getConfigInformation().get("parentField"))
                        .extracting(ConfigFieldInformation::getPosition)
                        .isEqualTo(0);
            }

            @Test
            @DisplayName("should throw if same field position is found")
            public void shouldThrowExceptionForSamePosition() {

                ActionFactory<String, ErrorConfig> factory = new ActionFactory<>(String.class, new Action<>() {
                    @Name("test")
                    @Config(ErrorConfig.class)
                    @Override
                    public void execute(String s, ActionContext<String, ErrorConfig> context) {

                    }
                });

                assertThatExceptionOfType(ArtObjectRegistrationException.class)
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

            assertThatCode(() -> actionFactory.initialize()).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should create an action context")
        public void shouldCreateActionContext() {

            ArtContext<String, TestConfig, ActionConfig<TestConfig>> context = actionFactory.create(new ActionConfig<>());

            assertThat(context).isNotNull();
            assertThat(context).extracting("action").isEqualTo(actionFactory.getArtObject());
            assertThat(context).extracting("targetClass").isEqualTo(actionFactory.getTargetClass());
            assertThat(context.getConfig()).isEmpty();
        }
    }

    @Name("Test")
    @Config(TestConfig.class)
    public static class TestAction implements Action<String, TestConfig> {
        @Override
        public void execute(String s, ActionContext<String, TestConfig> context) {

        }
    }

    public static class ConfigBase {

        @Position(0)
        private final String parentField = "foobar";
    }

    public static class TestConfig extends ConfigBase {

        private boolean noAnnotations;
        @Required
        @Position(1)
        private int required;
        @Description("World to teleport the player to.")
        private final String defaultField = "world";

        @Required
        @Description("Required field with default value.")
        private final double allAnnotations = 2.0d;

        @Ignore
        private final String ignored = "";

        private final NestedConfig nested = new NestedConfig();
    }

    public static class NestedConfig {
        @Description("nested config field")
        private final String nestedField = "foobar";
    }

    public static class ErrorConfig extends ConfigBase {

        @Position(0)
        private final int error = 2;
    }
}