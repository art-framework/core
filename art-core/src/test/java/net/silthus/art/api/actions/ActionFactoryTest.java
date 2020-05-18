package net.silthus.art.api.actions;

import jdk.internal.joptsimple.internal.Strings;
import net.silthus.art.api.annotations.Configurable;
import net.silthus.art.api.annotations.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ActionFactory")
public class ActionFactoryTest {

    private ActionFactory<String, String> factory;

    @BeforeEach
    public void beforeEach() {
        this.factory = new ActionFactory<>(String.class, new TestAction());
    }

    @Nested
    @DisplayName("initialize()")
    public class Initialize {

        @BeforeEach
        public void beforeEach() {

            assertThat(factory.getName()).isNullOrEmpty();
            assertThat(factory.getConfigInformation()).isEmpty();
        }

        @Test
        @DisplayName("should use annotations")
        public void shouldUseAnnotations() {

            assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            assertThat(factory.getName()).isEqualTo("Test");
            assertThat(factory.getConfigInformation()).containsExactly("test-string");
        }

        @Test
        @DisplayName("should not override manually set name and config information")
        public void shouldNotOverrideManualSetters() {

            factory.setName("foo");
            factory.setConfigInformation("bar");

            assertThatCode(() -> factory.initialize())
                    .doesNotThrowAnyException();

            assertThat(factory.getName()).isEqualTo("foo");
            assertThat(factory.getConfigInformation()).containsExactly("bar");
        }

        @Test
        @DisplayName("should throw ActionRegistrationException if missing annotations")
        public void shouldThrowIfMissingAnnotations() {

            factory = new ActionFactory<>(String.class, (s, context) -> {});

            assertThatExceptionOfType(ActionRegistrationException.class)
                    .isThrownBy(() -> factory.initialize());
        }

        @Test
        @DisplayName("should not throw if manual set but has missing annotations")
        public void shouldNotThrowIfNoAnnotationButManualInfo() {

            factory = new ActionFactory<>(String.class, (s, context) -> {});
            factory.setName("foo");
            factory.setConfigInformation("bar");

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getName()).isEqualTo("foo");
            assertThat(factory.getConfigInformation()).containsExactly("bar");
        }

        @Test
        @DisplayName("should not throw if missing config information")
        public void shouldNotThrowIfMissingConfigInformation() {

            factory = new ActionFactory<>(String.class, (s, context) -> {});
            factory.setName("foobar");

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getName())
                    .isEqualTo("foobar");
            assertThat(factory.getConfigInformation())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("should use annotations on method")
        public void shouldUseMethodAnnotation() {

            factory = new ActionFactory<>(String.class, new Action<String, String>() {
                @Name("foo")
                @Configurable({
                        "bar"
                })
                @Override
                public void execute(String s, ActionContext<String, String> context) {

                }
            });

            assertThatCode(() -> factory.initialize()).doesNotThrowAnyException();
            assertThat(factory.getName()).isEqualTo("foo");
            assertThat(factory.getConfigInformation()).containsExactly("bar");
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