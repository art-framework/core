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

package io.artframework.util;

import io.artframework.ConfigurationException;
import io.artframework.annotations.ConfigOption;
import io.artframework.annotations.Ignore;
import io.artframework.conf.ConfigFieldInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("ALL")
class ConfigUtilTest {

    @Nested
    class ConfigMap {

        @Test
        @DisplayName("should load all fields including superclass")
        public void shouldLoadAllFields() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .hasSizeGreaterThanOrEqualTo(4)
                    .containsKeys(
                            "parent_field",
                            "required",
                            "default_field",
                            "all_annotations"
                    )
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should load required annotation")
        public void shouldLoadRequiredAttribute() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .extractingByKey("required")
                    .extracting(ConfigFieldInformation::required)
                    .isEqualTo(true)
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should load description annotation")
        public void shouldLoadDescriptionAttribute() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .extractingByKey("default_field")
                    .extracting(ConfigFieldInformation::description)
                    .isEqualTo(new String[]{"World to teleport the player to."})
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should load default value")
        public void shouldLoadDefaultValue() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .extractingByKey("default_field")
                    .extracting(ConfigFieldInformation::defaultValue)
                    .isEqualTo("world")
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should load required field with default value")
        public void shouldLoadRequiredDefaultValue() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .extractingByKey("all_annotations")
                    .extracting(ConfigFieldInformation::defaultValue, ConfigFieldInformation::description)
                    .contains(2.0d, new String[]{"Required field with default value."})
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should load nested config objects")
        public void shouldLoadNestedObjects() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .extractingByKey("nested.nested_field")
                    .extracting(ConfigFieldInformation::description, ConfigFieldInformation::defaultValue)
                    .contains(new String[]{"nested config field"}, "foobar")
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should not load nested object fields")
        public void shouldNotAddNestedBase() {
            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .doesNotContainKey("nested")
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should ignore fields without an annotation")
        public void shouldIgnoredIgnored() {
            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .doesNotContainKeys("ignored", "no_annotations")
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should load field position annotation")
        public void shouldLoadFieldPosition() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(TestConfig.class))
                    .extractingByKeys("required", "parent_field")
                    .extracting(ConfigFieldInformation::position)
                    .contains(1, 0)
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should throw if same field position is found")
        public void shouldThrowExceptionForSamePosition() {

            assertThatExceptionOfType(ConfigurationException.class)
                    .isThrownBy(() -> ConfigUtil.getConfigFields(SamePositionConfig.class))
                    .withMessageContaining("same position");
        }

        @Test
        @DisplayName("should throw if declared config field is final")
        void shouldThrowIfConfigOptionIsFinal() {

            assertThatExceptionOfType(ConfigurationException.class)
                    .isThrownBy(() -> ConfigUtil.getConfigFields(FinalConfig.class))
                    .withMessageContaining("final field");
        }

        @Test
        @DisplayName("should load all fields if the class is annotated")
        void shouldLoadAllFieldsInAnnotatedClass() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(AnnotatedClass.class))
                    .containsOnlyKeys("foo", "bar")
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should load array field")
        void shouldLoadArrayField() {

            assertThatCode(() -> assertThat(ConfigUtil.getConfigFields(ArrayConfig.class))
                    .containsOnlyKeys("foo", "array")
            ).doesNotThrowAnyException();
        }
    }

    public static class SamePositionConfig {

        @ConfigOption(position = 1)
        private int pos1;
        @ConfigOption(position = 1)
        private int pos2;
    }


    public static class FinalConfig {

        @ConfigOption
        private final int myFinalField = 20;
    }

    public static class FinalConfigDefaultIgnore {

        private String foo;

        private final String bar = "is ignored";
    }

    public static class ConfigBase {

        @ConfigOption(position = 0)
        private String parentField = "foobar";
    }

    public static class TestConfig extends ConfigBase {

        private boolean noAnnotations;
        @ConfigOption(required = true, position = 1)
        private int required;
        @ConfigOption(description = "World to teleport the player to.")
        private String defaultField = "world";

        @ConfigOption(description = "Required field with default value.", required = true)
        private double allAnnotations = 2.0d;

        private String ignored = "";

        @ConfigOption
        private NestedConfig nested = new NestedConfig();

        private final String myFinalField = "is ignored";
    }

    public static class NestedConfig {
        @ConfigOption(description = "nested config field")
        private String nestedField = "foobar";
    }

    public static class ErrorConfig extends ConfigBase {

        @ConfigOption(position = 0)
        private int error = 2;
    }

    @ConfigOption
    public static class AnnotatedClass {

        private int foo;
        private String bar;
        @Ignore
        private double ignored;
    }

    @ConfigOption
    public static class ArrayConfig {

        private int foo;
        private String[] array;
    }

}