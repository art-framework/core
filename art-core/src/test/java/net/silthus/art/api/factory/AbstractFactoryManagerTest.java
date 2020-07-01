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

package net.silthus.art.api.factory;

import lombok.SneakyThrows;
import net.silthus.art.api.Action;
import net.silthus.art.api.ArtObject;
import net.silthus.art.api.ArtRegistrationException;
import net.silthus.art.api.actions.ActionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
@DisplayName("DefaultActionManager")
class AbstractFactoryManagerTest {

    private AbstractFactoryManager<ActionFactory<?, ?>> actionManager;

    @BeforeEach
    void beforeEach() {
        this.actionManager = new AbstractFactoryManager<ActionFactory<?, ?>>() {
        };
        actionManager.setLogger(Logger.getGlobal());
    }

    private <TFactory extends ArtFactory<?, ?, ArtObject, ?>> TFactory factory(String identifier, Class<TFactory> factoryClass) {
        TFactory factory = mock(factoryClass);
        when(factory.getIdentifier()).thenReturn(identifier);
        ArtObject action = mock(Action.class);
        when(factory.getArtObject()).thenReturn(action);
        return factory;
    }

    @Nested
    @DisplayName("exists(String)")
    class exists {

        @Test
        @DisplayName("should return false if action is not registered")
        void shouldNotExist() {

            assertThat(actionManager.exists("foobar"))
                    .isFalse();
        }

        @Test
        @DisplayName("should return true if action was registered")
        void shouldExist() {
            actionManager.getFactories().put("foobar", mock(ActionFactory.class));

            assertThat(actionManager.exists("foobar"))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("register(...)")
    class register {

        @Test
        @SneakyThrows
        @DisplayName("should store registered actions in memory")
        void shouldAddActionsToMemory() {

            actionManager.register(factory("foobar", ActionFactory.class));

            assertThat(actionManager.exists("foobar")).isTrue();
        }

        @Test
        @DisplayName("should not register actions with a duplicate identifier")
        void shouldNotAddDuplicateActions() {

            ActionFactory mock = factory("foobar", ActionFactory.class);
            assertThatCode(() -> actionManager.register(mock))
                    .doesNotThrowAnyException();

            assertThat(actionManager.getFactories().get("foobar"))
                    .isNotNull()
                    .extracting("identifier")
                    .isEqualTo("foobar");

            ActionFactory mock2 = factory("foobar", ActionFactory.class);
            assertThatExceptionOfType(ArtRegistrationException.class)
                    .isThrownBy(() -> actionManager.register(mock2))
                    .withMessageContaining("Duplicate ArtFactory for identifier \"foobar\" found.");
        }

        @Test
        @SneakyThrows
        @DisplayName("should register all actions inside the map")
        void shouldAddAllActionsInTheMap() {

            actionManager.register(Arrays.asList(
                    factory("test1", ActionFactory.class),
                    factory("test2", ActionFactory.class),
                    factory("test3", ActionFactory.class),
                    factory("test4", ActionFactory.class),
                    factory("test5", ActionFactory.class)
            ));

            assertThat(actionManager.getFactories())
                    .hasSize(5)
                    .containsKeys("test1", "test2", "test3", "test4", "test5");
        }

        @Test
        @SneakyThrows
        @DisplayName("should initialize factory")
        void shouldNotRegisterWithoutName() {

            ActionFactory foobar = factory("foobar", ActionFactory.class);

            assertThatCode(() -> actionManager.register(foobar))
                    .doesNotThrowAnyException();

            verify(foobar, times(1)).initialize();
        }
    }

    @Nested
    @DisplayName("getFactory(...)")
    class getFactory {

        @Test
        @DisplayName("should return wrapped optional")
        void shouldReturnWrappedOptional() {

            actionManager.getFactories().put("foobar", mock(ActionFactory.class));

            assertThat(actionManager.getFactory("foobar"))
                    .isNotEmpty();
        }

        @Test
        @DisplayName("should return empty optional if action factory does not exist")
        void shouldReturnEmptyOptional() {

            assertThat(actionManager.getFactory("foobar"))
                    .isEmpty();
        }
    }
}