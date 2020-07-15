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

package net.silthus.art.parser.flow.parser;

import lombok.SneakyThrows;
import net.silthus.art.Storage;
import net.silthus.art.impl.DefaultActionContext;
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

@DisplayName("ActionParser")
class ActionParserTest {

    private ActionParser parser;
    private ActionManager actionManager;
    private ActionFactory<?, ?> factory;

    @BeforeEach
    void beforeEach() {
        this.factory = mock(ActionFactory.class);
        this.actionManager = mock(ActionManager.class);
        when(actionManager.getFactory(anyString())).thenReturn(Optional.of(factory));
        when(factory.create(any())).thenAnswer(invocation -> new DefaultActionContext<>(Object.class, (o, context) -> {
        }, invocation.getArgument(0), null, mock(Storage.class)));

        this.parser = new ActionParser(actionManager);
    }

    @Nested
    @DisplayName("parse()")
    class parse {

        @Test
        @SneakyThrows
        @DisplayName("should match action identifier '!'")
        void shouldMatchActionIdentifier() {

            assertThat(parser.accept("!foobar")).isTrue();
            assertThat(parser.parse()).extracting(DefaultActionContext::getConfig)
                    .isEqualTo(Optional.empty());
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