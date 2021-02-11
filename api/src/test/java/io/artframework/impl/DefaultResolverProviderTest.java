package io.artframework.impl;

import io.artframework.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultResolverProviderTest {

    private DefaultResolverProvider provider;

    @BeforeEach
    void setUp() {

        provider = new DefaultResolverProvider(ART.globalScope());
    }

    @Test
    @DisplayName("should register provided resolver class")
    void shouldRegisterResolverType() {

        provider.add(TestResolver.class);

        assertThat(provider.get(String.class, TestResolver.class))
                .isNotEmpty().get()
                .extracting(ResolverFactory::resolverClass)
                .isEqualTo(TestResolver.class);
    }

    @Test
    @DisplayName("should find resolver by type")
    void shouldGetResolverByType() {

        provider.add(TestResolver.class);

        assertThat(provider.get(String.class))
                .isNotEmpty().get()
                .extracting(ResolverFactory::resolverClass)
                .isEqualTo(TestResolver.class);
    }

    public static class TestResolver implements Resolver<String> {
        @Override
        public String resolve(ResolveContext context) throws ResolveException {
            return null;
        }
    }

}