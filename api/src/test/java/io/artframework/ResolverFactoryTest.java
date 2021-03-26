package io.artframework;

import io.artframework.annotations.ConfigOption;
import io.artframework.conf.KeyValuePair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

class ResolverFactoryTest {

    @Nested
    @DisplayName("ResolverFactory.of(Class<?>)")
    class FromClass {

        @Test
        @DisplayName("should create a resolver factory from a class")
        void shouldCreateResolverFactoryFromClass() {

            assertThatCode(() -> assertThat(ResolverFactory.of(ART.scope(), TestResolver.class))
                    .isNotNull()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should throw if resolver cannot be created")
        void shouldThrowIfResolverHasNoConstructor() {

            assertThatExceptionOfType(ConfigurationException.class)
                    .isThrownBy(() -> ResolverFactory.of(ART.scope(), ErrorResolver.class));
        }
    }

    @Nested
    @DisplayName("ResolverFactory.of(Class<?>, Supplier)")
    class OfSupplier {

        @Test
        @DisplayName("should create resolver factory with supplier")
        void shouldCreateResolverFactory() {

            assertThatCode(() -> assertThat(ResolverFactory.of(ART.scope(), TestResolver.class, TestResolver::new))
                    .isNotNull()
            ).doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("should map the config options to the created instance")
    void shouldCreateAndMapTheConfigOptions() throws ConfigurationException {

        Resolver<String> resolver = ResolverFactory.of(ART.scope(), TestResolver.class).create(Arrays.asList(
                KeyValuePair.of("test", "foobar"),
                KeyValuePair.of("foo", "bar")
        ));

        assertThat(resolver)
                .isNotNull()
                .extracting("test")
                .isEqualTo("foobar");
    }

    public static class TestResolver implements Resolver<String> {

        @ConfigOption
        private String test;

        @Override
        public String resolve(ResolveContext context) throws ResolveException {
            return null;
        }
    }

    public static class ErrorResolver implements Resolver<String> {

        public ErrorResolver(String foobar) {
        }

        @Override
        public String resolve(ResolveContext context) throws ResolveException {
            return null;
        }
    }


}