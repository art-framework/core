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

package net.silthus.art.api.trigger;

import lombok.SneakyThrows;
import net.silthus.art.api.ArtRegistrationException;
import net.silthus.art.api.Trigger;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.api.scheduler.Scheduler;
import net.silthus.art.api.storage.StorageProvider;
import net.silthus.art.storage.MemoryStorageProvider;
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
    private StorageProvider storageProvider;

    @BeforeEach
    void beforeEach() {
        scheduler = mock(Scheduler.class);
        storageProvider = new MemoryStorageProvider();
    }

    @Nested
    @DisplayName("initialize()")
    class initialize {

        @Test
        @SneakyThrows
        @DisplayName("should initialize factory with single method")
        void shouldInitializeWithSingleMethod() {

            MyMultiTrigger trigger = new MyMultiTrigger();
            TriggerFactory<Object> factory = new TriggerFactory<>(trigger, storageProvider, scheduler);
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

            TriggerFactory<Object> factory = new TriggerFactory<>(new MySecondTrigger(), storageProvider, scheduler);

            assertThatCode(factory::initialize)
                    .doesNotThrowAnyException();

            assertThat(factory)
                    .extracting(ArtFactory::getIdentifier, ArtFactory::getDescription)
                    .contains("test", new String[0]);
        }

        @Test
        @DisplayName("should fail initialization if no class or method annotation exists")
        void shouldFailToInitializeIfNoClassAnnotationExists() {

            TriggerFactory<Object> factory = new TriggerFactory<>(new MyFailingTrigger(), storageProvider, null);

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

            TriggerFactory<Object> factory = new TriggerFactory<>(new MyFailingTrigger(), storageProvider, null);

            TriggerContext context1 = factory.create(new TriggerConfig<>());
            TriggerContext context2 = factory.create(new TriggerConfig<>());

            assertThat(factory.getCreatedTrigger())
                    .containsExactly(context1, context2);
        }
    }

    @Name("test")
    static class MyTrigger implements Trigger {

        public void myMethod() {

        }
    }

    @Name("test")
    static class MySecondTrigger implements Trigger {

        @Name("method")
        @Description("foobar")
        public void myMethod() {

        }
    }
    
    static class MyMultiTrigger implements Trigger {

        @Name("one")
        @Description("test")
        public void methodOne() {
            
        }

        @Name("two")
        public void methodTwo() {

        }

        @Name("three")
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