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

import io.artframework.ActionContext;
import io.artframework.ActionFactory;
import io.artframework.ActionProvider;
import io.artframework.ArtObjectMeta;
import io.artframework.impl.DefaultScope;
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
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
@DisplayName("ActionParser")
class ActionParserTest {

    private ActionLineParser parser;
    private ActionFactory<?> actionFactory;

    @BeforeEach
    void beforeEach() {
        actionFactory = mock(ActionFactory.class);
        when(actionFactory.meta()).thenReturn(mock(ArtObjectMeta.class));

        ActionProvider actionProvider = mock(ActionProvider.class);
        when(actionProvider.get(anyString())).thenReturn(Optional.of(actionFactory));
        when(actionFactory.createContext(any(), any())).thenReturn(mock(ActionContext.class));

        this.parser = new ActionLineParser(Arrays.asList("").iterator(), new DefaultScope(configurationBuilder -> configurationBuilder.actions(actionProvider)));
    }

    @Nested
    @DisplayName("parse()")
    class parse {

        @Test
        @SneakyThrows
        @DisplayName("should match action identifier '!'")
        void shouldMatchActionIdentifier() {

            assertThat(parser.accept("!foobar")).isTrue();

            assertThatCode(() -> parser.parse()).doesNotThrowAnyException();

            verify(actionFactory, times(1)).createContext(any(), any());
        }

        @ParameterizedTest
        @SneakyThrows
        @DisplayName("should not match other identifier: ")
        @ValueSource(chars = {'?', '@', ':', '~', '#', '-', '+', '*', '_', '<', '>', '|'})
        void shouldNotMatchOtherIdentifiers(char identifier) {

            assertThat(parser.accept(identifier + "foobar")).isFalse();
        }
    }
}