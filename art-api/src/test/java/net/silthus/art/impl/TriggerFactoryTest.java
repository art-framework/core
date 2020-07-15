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

package net.silthus.art.impl;

import lombok.SneakyThrows;
import net.silthus.art.ArtOptions;
import net.silthus.art.Scheduler;
import net.silthus.art.Storage;
import net.silthus.art.Trigger;
import net.silthus.art.api.ArtRegistrationException;
import net.silthus.art.api.trigger.DefaultTriggerContext;
import net.silthus.art.api.trigger.TriggerFactory;
import net.silthus.art.conf.TriggerConfig;
import net.silthus.art.impl.DefaultMapStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("TriggerFactory")
@SuppressWarnings({"rawtypes", "unused"})
class TriggerFactoryTest {

    private Scheduler scheduler;
    private Storage storage;

    @BeforeEach
    void beforeEach() {
        scheduler = mock(Scheduler.class);
        storage = new DefaultMapStorage();
    }

    @Nested
    @DisplayName("initialize()")
    class initialize {

        @Test
        @SneakyThrows
        @DisplayName("should initialize factory with single method")
        void shouldInitializeWithSingleMethod() {

            MyMultiTrigger trigger = new MyMultiTrigger();
            TriggerFactory<Object> factory = new TriggerFactory<>(trigger, storage, scheduler);
            factory.setMethod(trigger.getClass().getDeclaredMethod("methodTwo"));

            assertThatCode(factory::initialize)
                    .doesNotThrowAnyException();

            assertThat(factory)
                    .extracting(ArtFactory::getIdentifier, ArtFactory::getDescription)
                    .contains("two", new String[0]);
        }

        @Test
        @SneakyThrows
        @DisplayName("should initialize factory with class annotation only")
        void shouldInitializeWithClassAnnotation() {

            TriggerFactory<Object> factory = new TriggerFactory<>(new MySecondTrigger(), storage, scheduler);

            assertThatCode(factory::initialize)
                    .doesNotThrowAnyException();

            assertThat(factory)
                    .extracting(ArtFactory::getIdentifier, ArtFactory::getDescription)
                    .contains("test", new String[0]);
        }

        @Test
        @DisplayName("should fail initialization if no class or method annotation exists")
        void shouldFailToInitializeIfNoClassAnnotationExists() {

            TriggerFactory<Object> factory = new TriggerFactory<>(new MyFailingTrigger(), storage, null);

            assertThatExceptionOfType(ArtRegistrationException.class)
                    .isThrownBy(factory::initialize)
                    .withMessageContaining("has no defined name");
        }
    }

    @Nested
    @DisplayName("create(...)")
    class create {

        @Test
        @DisplayName("should store context in factory")
        void shouldStoreContextInFactory() {

            TriggerFactory<Object> factory = new TriggerFactory<>(new MyFailingTrigger(), storage, null);

            DefaultTriggerContext context1 = factory.create(new TriggerConfig<>());
            DefaultTriggerContext context2 = factory.create(new TriggerConfig<>());

            assertThat(factory.getCreatedTrigger())
                    .containsExactly(context1, context2);
        }
    }

    @ArtOptions("test")
    static class MyTrigger implements Trigger {

        public void myMethod() {

        }
    }

    @ArtOptions("test")
    static class MySecondTrigger implements Trigger {

        @ArtOptions(value = "method", description = "foobar")
        public void myMethod() {

        }
    }
    
    static class MyMultiTrigger implements Trigger {

        @ArtOptions(value = "one", description = "test")
        public void methodOne() {
            
        }

        @ArtOptions("two")
        public void methodTwo() {

        }

        @ArtOptions("three")
        public void methodThree() {

        }
    }

    static class MyFailingTrigger implements Trigger {

        public void onTrigger() {

        }

        public void foobar() {

        }
    }
}