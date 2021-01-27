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

package io.artframework.parser.flow;

import io.artframework.ArtObjectMeta;
import io.artframework.RequirementContext;
import io.artframework.RequirementFactory;
import io.artframework.RequirementProvider;
import io.artframework.Scope;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
@DisplayName("RequirementParser")
class RequirementParserTest {

    private RequirementLineParser parser;
    private RequirementFactory<?> requirementFactory;

    @BeforeEach
    void beforeEach() {
        requirementFactory = mock(RequirementFactory.class);
        when(requirementFactory.meta()).thenReturn(mock(ArtObjectMeta.class));

        RequirementProvider requirementProvider = mock(RequirementProvider.class);
        when(requirementProvider.get(anyString())).thenReturn(Optional.of(requirementFactory));
        when(requirementFactory.createContext(any())).thenReturn(mock(RequirementContext.class));

        this.parser = new RequirementLineParser(Arrays.asList("").iterator(), Scope.of(configurationBuilder -> configurationBuilder.requirements(requirementProvider)));
    }

    @Nested
    @DisplayName("parse()")
    class parse {

        @Test
        @SneakyThrows
        @DisplayName("should match requirement identifier '?'")
        void shouldMatchActionIdentifier() {

            assertThat(parser.accept("?foobar")).isTrue();

            assertThatCode(() -> parser.parse()).doesNotThrowAnyException();

            verify(requirementFactory, times(1)).createContext(any());
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