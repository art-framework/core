package net.silthus.art.api.actions;

import net.silthus.art.api.ARTObjectRegistrationException;
import net.silthus.art.api.annotations.Config;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.annotations.Required;
import net.silthus.art.api.config.ConfigFieldInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ActionFactory")
public class ActionFactoryTest {

    private ActionFactory<String, TestConfig> factory;

    @BeforeEach
    public void beforeEach() {
        this.factory = new ActionFactory<>(String.class, new TestAction());
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

            factory = new ActionFactory<>(String.class, (s, context) -> {
            });

            assertThatExceptionOfType(ARTObjectRegistrationException.class)
                    .isThrownBy(() -> factory.initialize());
        }

        @Test
        @DisplayName("should not throw if manual set but has missing annotations")
        public void shouldNotThrowIfNoAnnotationButManualInfo() {

            factory = new ActionFactory<>(String.class, (s, context) -> {
            });
            factory.setIdentifier("foo");
            factory.setConfigClass(TestConfig.class);

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigClass()).contains(TestConfig.class);
        }

        @Test
        @DisplayName("should not throw if missing config information")
        public void shouldNotThrowIfMissingConfigInformation() {

            factory = new ActionFactory<>(String.class, (s, context) -> {
            });
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

            factory = new ActionFactory<>(String.class, new Action<>() {

                @Name("foo")
                @Config(TestConfig.class)
                @Override
                public void execute(String s, ActionContext<String, TestConfig> context) {

                }
            });

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigClass()).contains(TestConfig.class);
        }

        @Nested
        @DisplayName("creates ConfigFieldInformation that")
        class ConfigAnnotations {

            @Test
            @DisplayName("should load all fields including superclass")
            public void shouldLoadAllFields() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation())
                        .hasSize(5)
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

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("required"))
                        .extracting(ConfigFieldInformation::isRequired)
                        .isEqualTo(true);
            }

            @Test
            @DisplayName("should load description annotation")
            public void shouldLoadDescriptionAttribute() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("defaultField"))
                        .extracting(ConfigFieldInformation::getDescription)
                        .isEqualTo("World to teleport the player to.");
            }

            @Test
            @DisplayName("should load default value")
            public void shouldLoadDefaultValue() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("defaultField"))
                        .extracting(ConfigFieldInformation::getDefaultValue)
                        .isEqualTo("world");
            }

            @Test
            @DisplayName("should load required field with default value")
            public void shouldLoadRequiredDefaultValue() {

                assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
                assertThat(factory.getConfigInformation().get("allAnnotations"))
                        .extracting(ConfigFieldInformation::getDefaultValue, ConfigFieldInformation::getDescription)
                        .contains(2.0d, "Required field with default value.");
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

        @Test
        @DisplayName("should create an action context")
        public void shouldCreateActionContext() {

            ActionContext<String, TestConfig> context = factory.create(new ActionConfig<>());

            assertThat(context).isNotNull();
            assertThat(context.getAction().equals(factory.getArtObject()));
            assertThat(context.getTargetClass()).isEqualTo(factory.getTargetClass());
            assertThat(context.getConfig()).isEmpty();
        }

        @Test
        @DisplayName("should not cache action context if config is different")
        public void shouldNotCacheContext() {

            ActionConfig<TestConfig> config1 = new ActionConfig<>();
            config1.setCooldown("2s");
            ActionContext<String, TestConfig> context1 = factory.create(config1);
            ActionContext<String, TestConfig> context2 = factory.create(new ActionConfig<>());

            assertThat(context1).isNotNull();
            assertThat(context2).isNotNull();

            assertThat(context1).isNotSameAs(context2);
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

        private String parentField = "foobar";
    }

    public static class TestConfig extends ConfigBase {

        private boolean noAnnotations;
        @Required
        private int required;
        @Description("World to teleport the player to.")
        private String defaultField = "world";

        @Required
        @Description("Required field with default value.")
        private double allAnnotations = 2.0d;
    }
}