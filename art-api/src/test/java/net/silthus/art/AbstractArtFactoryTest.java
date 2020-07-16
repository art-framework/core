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

import net.silthus.art.conf.ConfigFieldInformation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class AbstractArtFactoryTest {

    private Configuration configuration;

    private AbstractArtFactory<?, ?, ?> factory;

    @BeforeEach
    void setUp() {
        configuration = mock(Configuration.class);
        factory = factory(Object.class);
    }

    private <TTarget> AbstractArtFactory<?, ?, ?> factory(Class<TTarget> targetClass) {
        return factory(targetClass, TestArtObject.class);
    }

    private <TTarget, TObject extends ArtObject> AbstractArtFactory<?, ?, ?> factory(Class<TTarget> targetClass, Class<TObject> objectClass) {
        return new AbstractArtFactory<TTarget, ArtObjectContext, TObject>(configuration, targetClass, objectClass) {
            @Override
            public ArtObjectContext create(Consumer<ArtObjectContext> initialization) {
                return mock(ArtObjectContext.class);
            }
        };
    }

    @Nested
    @DisplayName("initialize()")
    public class Initialize {


        @BeforeEach
        public void beforeEach() {

            Assertions.assertThat(factory.getIdentifier()).isNullOrEmpty();
            Assertions.assertThat(factory.getConfigClass()).isNull();
        }

        @Test
        @DisplayName("should use annotations")
        public void shouldUseAnnotations() {

            Assertions.assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            Assertions.assertThat(factory.getIdentifier()).isEqualTo("Test");
            Assertions.assertThat(factory.getConfigClass()).isEqualTo(ArtFactoryTest.TestConfig.class);
        }

        @Test
        @DisplayName("should use description annotation on class")
        public void shouldUseDescriptionAnnotation() {

            Assertions.assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            Assertions.assertThat(factory.getDescription()).isEqualTo(new String[]{"Description"});
        }

        @Test
        @DisplayName("should not override manually set name and config information")
        public void shouldNotOverrideManualSetters() {

            factory.setIdentifier("foo");
            factory.setConfigClass(null);

            Assertions.assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            Assertions.assertThat(factory.getIdentifier()).isEqualTo("foo");
            Assertions.assertThat(factory.getConfigClass()).isEqualTo(ArtFactoryTest.TestConfig.class);
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
            factory.setConfigClass(ArtFactoryTest.TestConfig.class);

            Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            Assertions.assertThat(factory.getIdentifier()).isEqualTo("foo");
            Assertions.assertThat(factory.getConfigClass()).isEqualTo(ArtFactoryTest.TestConfig.class);
        }

        @Test
        @DisplayName("should not throw if missing config information")
        public void shouldNotThrowIfMissingConfigInformation() {

            factory = factory(String.class);
            factory.setIdentifier("foobar");

            Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            Assertions.assertThat(factory.getIdentifier())
                    .isEqualTo("foobar");
            Assertions.assertThat(factory.getConfigClass())
                    .isNull();
        }

        @Test
        @DisplayName("should use annotations on method")
        public void shouldUseMethodAnnotation() {

            factory = factory(String.class, MethodArtObject.class);

            Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            Assertions.assertThat(factory.getIdentifier()).isEqualTo("foo");
            Assertions.assertThat(factory.getConfigClass()).isEqualTo(ArtFactoryTest.TestConfig.class);
        }

        @SuppressWarnings("unchecked")
        @Nested
        @DisplayName("creates ConfigFieldInformation that")
        class ConfigAnnotations {

            @Test
            @DisplayName("should load all fields including superclass")
            public void shouldLoadAllFields() {

                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
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

                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("required"))
                        .extracting(ConfigFieldInformation::isRequired)
                        .isEqualTo(true);
            }

            @Test
            @DisplayName("should load description annotation")
            public void shouldLoadDescriptionAttribute() {

                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("default_field"))
                        .extracting(ConfigFieldInformation::getDescription)
                        .isEqualTo(new String[]{"World to teleport the player to."});
            }

            @Test
            @DisplayName("should load default value")
            public void shouldLoadDefaultValue() {

                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("default_field"))
                        .extracting(ConfigFieldInformation::getDefaultValue)
                        .isEqualTo("world");
            }

            @Test
            @DisplayName("should load required field with default value")
            public void shouldLoadRequiredDefaultValue() {

                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("all_annotations"))
                        .extracting(ConfigFieldInformation::getDefaultValue, ConfigFieldInformation::getDescription)
                        .contains(2.0d, new String[]{"Required field with default value."});
            }

            @Test
            @DisplayName("should load nested config objects")
            public void shouldLoadNestedObjects() {

                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation())
                        .containsKeys("nested.nested_field");
                assertThat(factory.getConfigInformation().get("nested.nested_field"))
                        .extracting(ConfigFieldInformation::getDescription, ConfigFieldInformation::getDefaultValue)
                        .contains(new String[]{"nested config field"}, "foobar");
            }

            @Test
            @DisplayName("should not load nested object fields")
            public void shouldNotAddNestedBase() {
                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation())
                        .doesNotContainKey("nested");
            }

            @Test
            @DisplayName("should ignore @Ignored fields")
            public void shouldIgnoredIgnored() {
                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation())
                        .doesNotContainKey("ignored");
            }

            @Test
            @DisplayName("should load field position annotation")
            public void shouldLoadFieldPosition() {

                Assertions.assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
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


                assertThatExceptionOfType(ArtObjectInformationException.class)
                        .isThrownBy(factory::initialize)
                        .withMessageContaining("found same position");
            }
        }
    }

    @Nested
    @DisplayName("createArtObject()")
    class createArtObject {
    }

    @ArtOptions("test")
    public static class TestArtObject implements ArtObject {

    }

    public static class MethodArtObject implements ArtObject {

        @ArtOptions("foo")
        public void foo() {}
    }
}