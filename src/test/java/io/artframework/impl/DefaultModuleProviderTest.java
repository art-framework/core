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

package io.artframework.impl;

import io.artframework.Configuration;
import io.artframework.Module;
import io.artframework.ModuleRegistrationException;
import io.artframework.annotations.ART;
import io.artframework.annotations.Depends;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class DefaultModuleProviderTest {

    DefaultModuleProvider provider;
    TestModule module;

    @BeforeEach
    void setUp() {
        Configuration configuration = mock(Configuration.class);
        provider = new DefaultModuleProvider(configuration);
        module = spy(new TestModule());
    }

    @Nested
    @DisplayName("load(...)")
    class load {

        @Test
        @SneakyThrows
        @DisplayName("should not enable already enabled modules")
        void shouldNotLoadModuleIfAlreadyEnabled() {

            provider.load(module);
            provider.load(module);

            verify(module, times(1)).onEnable(any());
        }

        @Test
        @DisplayName("should throw if a module with the same identifier and different class exists")
        void shouldThrowIfDuplicateModuleExists() {

            assertThatCode(() -> provider.load(module)).doesNotThrowAnyException();
            assertThatExceptionOfType(ModuleRegistrationException.class)
                    .isThrownBy(() -> provider.load(new DuplicateModule()))
                    .withMessageContaining("There is already a module named \"test\" registered");
        }

        @Test
        @DisplayName("should throw if module is missing @ART annotation")
        void shouldThrowIfModuleIsMissingAnnotation() {

            assertThatExceptionOfType(ModuleRegistrationException.class)
                    .isThrownBy(() -> provider.load(new MissingAnnotationModule()))
                    .withMessageContaining("missing the required @ART annotation");
        }

        @Test
        @DisplayName("should throw if module has missing dependencies")
        void shouldThrowIfModuleHasMissingDependencies() {

            assertThatExceptionOfType(ModuleRegistrationException.class)
                    .isThrownBy(() -> provider.load(new FooModule()))
                    .withMessageContaining("is missing the following dependencies: bar");

        }

        @Test
        @DisplayName("should enable the child modules first")
        void shouldEnableTheChildModulesFirst() {

            BarModule barModule = spy(new BarModule());
            FooModule fooModule = spy(new FooModule());

            assertThatCode(() -> provider.register(barModule)).doesNotThrowAnyException();
            assertThatCode(() -> provider.load(fooModule)).doesNotThrowAnyException();

            InOrder inOrder = inOrder(barModule, fooModule);
            inOrder.verify(barModule, times(1)).onEnable(any());
            inOrder.verify(fooModule, times(1)).onEnable(any());
        }

        @Test
        @DisplayName("should throw if a module has cyclic dependencies")
        void shouldThrowIfCyclicDependenciesExist() {

            assertThatCode(() -> provider.register(new FooModule())).doesNotThrowAnyException();
            assertThatCode(() -> provider.register(new BarModule())).doesNotThrowAnyException();
            assertThatCode(() -> provider.register(new Module1())).doesNotThrowAnyException();
            assertThatCode(() -> provider.register(new Module2())).doesNotThrowAnyException();

            assertThatExceptionOfType(ModuleRegistrationException.class)
                    .isThrownBy(() -> provider.register(new Module3()))
                    .withMessageContaining("cyclic dependencies");
        }
    }

    @ART("test")
    static class TestModule implements Module {
        @Override
        public void onEnable(Configuration configuration) {

        }

        @Override
        public void onDisable(Configuration configuration) {

        }
    }

    @ART("test")
    static class DuplicateModule implements Module {

        @Override
        public void onEnable(Configuration configuration) {

        }

        @Override
        public void onDisable(Configuration configuration) {

        }
    }

    static class MissingAnnotationModule implements Module {
        @Override
        public void onEnable(Configuration configuration) {

        }

        @Override
        public void onDisable(Configuration configuration) {

        }
    }

    @ART("foo")
    @Depends("bar")
    static class FooModule implements Module {
        @Override
        public void onEnable(Configuration configuration) {

        }

        @Override
        public void onDisable(Configuration configuration) {

        }
    }

    @ART("bar")
    static class BarModule implements Module {
        @Override
        public void onEnable(Configuration configuration) {

        }

        @Override
        public void onDisable(Configuration configuration) {

        }
    }

    @ART("module 1")
    @Depends({"module 2", "foo"})
    static class Module1 implements Module {

        @Override
        public void onEnable(Configuration configuration) {

        }

        @Override
        public void onDisable(Configuration configuration) {

        }
    }

    @ART("module 2")
    @Depends("module 3")
    static class Module2 implements Module {

        @Override
        public void onEnable(Configuration configuration) {

        }

        @Override
        public void onDisable(Configuration configuration) {

        }
    }

    @ART("module 3")
    @Depends("module 1")
    static class Module3 implements Module {

        @Override
        public void onEnable(Configuration configuration) {

        }

        @Override
        public void onDisable(Configuration configuration) {

        }
    }
}