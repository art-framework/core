package net.silthus.art.api.actions;

import net.silthus.art.api.ARTObjectRegistrationException;
import net.silthus.art.api.annotations.Config;
import net.silthus.art.api.annotations.Name;
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

            factory = new ActionFactory<>(String.class, (s, context) -> {});

            assertThatExceptionOfType(ARTObjectRegistrationException.class)
                    .isThrownBy(() -> factory.initialize());
        }

        @Test
        @DisplayName("should not throw if manual set but has missing annotations")
        public void shouldNotThrowIfNoAnnotationButManualInfo() {

            factory = new ActionFactory<>(String.class, (s, context) -> {});
            factory.setIdentifier("foo");
            factory.setConfigClass(TestConfig.class);

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigClass()).contains(TestConfig.class);
        }

        @Test
        @DisplayName("should not throw if missing config information")
        public void shouldNotThrowIfMissingConfigInformation() {

            factory = new ActionFactory<>(String.class, (s, context) -> {});
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
            assertThat(context.getConfig()).isNull();
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

    public static class TestConfig {

    }
}