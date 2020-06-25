package net.silthus.art.parser.flow.parser;

import lombok.SneakyThrows;
import net.silthus.art.RequirementContext;
import net.silthus.art.api.requirements.RequirementFactory;
import net.silthus.art.api.requirements.RequirementManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("RequirementParser")
class RequirementParserTest {

    private RequirementParser parser;
    private RequirementManager requirementManager;
    private RequirementFactory<?, ?> factory;

    @BeforeEach
    void beforeEach() {
        this.factory = mock(RequirementFactory.class);
        this.requirementManager = mock(RequirementManager.class);
        when(requirementManager.getFactory(anyString())).thenReturn(Optional.of(factory));
        when(factory.create(any())).thenAnswer(invocation -> new RequirementContext<>(Object.class, (o, context) -> true, invocation.getArgument(0)));

        this.parser = new RequirementParser(requirementManager);
    }

    @Nested
    @DisplayName("parse()")
    class parse {

        @Test
        @SneakyThrows
        @DisplayName("should match action identifier '?'")
        void shouldMatchActionIdentifier() {

            assertThat(parser.accept("?foobar")).isTrue();
            assertThat(parser.parse()).extracting(RequirementContext::getConfig)
                    .isEqualTo(Optional.empty());
        }

        @ParameterizedTest
        @SneakyThrows
        @DisplayName("should not match other identifier: ")
        @ValueSource(chars = {'!', '@', ':', '~', '#', '-', '+', '*', '_', '<', '>', '|'})
        void shouldNotMatchOtherIdentifiers(char identifier) {

            assertThat(parser.accept(identifier + "foobar")).isFalse();
        }
    }
}