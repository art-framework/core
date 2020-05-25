package net.silthus.art.api.actions;

import net.silthus.art.api.ARTObjectRegistrationException;
import net.silthus.art.api.annotations.Configurable;
import net.silthus.art.api.annotations.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ActionFactory")
public class ActionFactoryTest {

    private ActionFactory<String, String> factory;

    @BeforeEach
    public void beforeEach() {
        this.factory = new ActionFactory<>(String.class, String.class, new TestAction());
    }

    @Nested
    @DisplayName("initialize()")
    public class Initialize {

        @BeforeEach
        public void beforeEach() {

            assertThat(factory.getIdentifier()).isNullOrEmpty();
            assertThat(factory.getConfigInformation()).isEmpty();
        }

        @Test
        @DisplayName("should use annotations")
        public void shouldUseAnnotations() {

            assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            assertThat(factory.getIdentifier()).isEqualTo("Test");
            assertThat(factory.getConfigInformation()).containsExactly("test-string");
        }

        @Test
        @DisplayName("should not override manually set name and config information")
        public void shouldNotOverrideManualSetters() {

            factory.setIdentifier("foo");
            factory.setConfigInformation("bar");

            assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigInformation()).containsExactly("bar");
        }

        @Test
        @DisplayName("should throw ActionRegistrationException if missing annotations")
        public void shouldThrowIfMissingAnnotations() {

            factory = new ActionFactory<>(String.class, String.class, (s, context) -> {});

            assertThatExceptionOfType(ARTObjectRegistrationException.class)
                    .isThrownBy(() -> factory.initialize());
        }

        @Test
        @DisplayName("should not throw if manual set but has missing annotations")
        public void shouldNotThrowIfNoAnnotationButManualInfo() {

            factory = new ActionFactory<>(String.class, String.class, (s, context) -> {});
            factory.setIdentifier("foo");
            factory.setConfigInformation("bar");

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigInformation()).containsExactly("bar");
        }

        @Test
        @DisplayName("should not throw if missing config information")
        public void shouldNotThrowIfMissingConfigInformation() {

            factory = new ActionFactory<>(String.class, String.class, (s, context) -> {});
            factory.setIdentifier("foobar");

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier())
                    .isEqualTo("foobar");
            assertThat(factory.getConfigInformation())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("should use annotations on method")
        public void shouldUseMethodAnnotation() {

            factory = new ActionFactory<>(String.class, String.class, new Action<String, String>() {
                @Name("foo")
                @Configurable({
                        "bar"
                })
                @Override
                public void execute(String s, ActionContext<String, String> context) {

                }
            });

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getIdentifier()).isEqualTo("foo");
            assertThat(factory.getConfigInformation()).containsExactly("bar");
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

            ActionContext<String, String> context = factory.create(new ActionConfig<>());

            assertThat(context).isNotNull();
            assertThat(context.getAction().equals(factory.getArtObject()));
            assertThat(context.getTargetClass()).isEqualTo(factory.getTargetClass());
            assertThat(context.getConfig()).isEmpty();
        }

        @Test
        @DisplayName("should not cache action context if config is different")
        public void shouldNotCacheContext() {

            ActionConfig<String> config1 = new ActionConfig<>();
            config1.setCooldown("2s");
            ActionContext<String, String> context1 = factory.create(config1);
            ActionContext<String, String> context2 = factory.create(new ActionConfig<>());

            assertThat(context1).isNotNull();
            assertThat(context2).isNotNull();

            assertThat(context1).isNotSameAs(context2);
        }
    }

    @Name("Test")
    @Configurable({
            "test-string"
    })
    public static class TestAction implements Action<String, String> {
        @Override
        public void execute(String s, ActionContext<String, String> context) {

        }
    }

}