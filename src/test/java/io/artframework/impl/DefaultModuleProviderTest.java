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
import io.artframework.ModuleRegistrationException;
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;
import io.artframework.annotations.OnDisable;
import io.artframework.annotations.OnEnable;
import lombok.SneakyThrows;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
class DefaultModuleProviderTest {

    DefaultModuleProvider provider;
    TestModule module;

    @BeforeEach
    void setUp() {
        Scope scope = Scope.defaultScope();
        provider = new DefaultModuleProvider(scope);
        module = spy(new TestModule());
    }

    @Nested
    @DisplayName("load(...)")
    class load {

        @Test
        @SneakyThrows
        @DisplayName("should not enable already enabled modules")
        void shouldNotLoadModuleIfAlreadyEnabled() {

            provider.enable(module);
            provider.enable(module);

            verify(module, times(1)).onEnable(any());
        }

        @Test
        @DisplayName("should throw if a module with the same identifier and different class exists")
        void shouldThrowIfDuplicateModuleExists() {

            assertThatCode(() -> provider.enable(module)).doesNotThrowAnyException();
            assertThatExceptionOfType(ModuleRegistrationException.class)
                    .isThrownBy(() -> provider.enable(new DuplicateModule()))
                    .withMessageContaining("There is already a module named \"test\" registered");
        }

        @Test
        @DisplayName("should throw if module is missing @ART annotation")
        void shouldThrowIfModuleIsMissingAnnotation() {

            assertThatExceptionOfType(ModuleRegistrationException.class)
                    .isThrownBy(() -> provider.enable(new MissingAnnotationModule()))
                    .withMessageContaining("missing the required @Module annotation");
        }

        @Test
        @DisplayName("should throw if module has missing dependencies")
        void shouldThrowIfModuleHasMissingDependencies() {

            assertThatExceptionOfType(ModuleRegistrationException.class)
                    .isThrownBy(() -> provider.enable(new FooModule()))
                    .withMessageContaining("is missing the following dependencies: bar");

        }

        @Test
        @DisplayName("should enable the child modules first")
        void shouldEnableTheChildModulesFirst() {

            BarModule barModule = spy(new BarModule());
            FooModule fooModule = spy(new FooModule());

            assertThatCode(() -> provider.register(barModule)).doesNotThrowAnyException();
            assertThatCode(() -> provider.enable(fooModule)).doesNotThrowAnyException();

            InOrder inOrder = inOrder(barModule, fooModule);
            inOrder.verify(barModule, times(1)).onEnable(any());
            inOrder.verify(fooModule, times(1)).onEnable(any());
        }

        @SneakyThrows
        @Test
        @Disabled
        @DisplayName("should throw if a module has cyclic dependencies")
        void shouldThrowIfCyclicDependenciesExist() {

            assertThatCode(() -> provider.register(new FooModule())).doesNotThrowAnyException();
            assertThatCode(() -> provider.register(new BarModule())).doesNotThrowAnyException();
            assertThatCode(() -> provider.register(new Module1())).doesNotThrowAnyException();
            assertThatCode(() -> provider.register(new Module2())).doesNotThrowAnyException();

            assertThatThrownBy(() -> provider.register(new Module3()))
                    .hasMessageContaining("cyclic dependencies");
        }

        @Test
        @DisplayName("should register module")
        void shouldRegisterModule() {

            assertThatCode(() -> provider.register(module)).doesNotThrowAnyException();
            assertThat(provider.modules)
                    .hasSize(1)
                    .extractingByKey(module.getClass())
                    .isNotNull()
                    .extracting(moduleInformation -> moduleInformation.moduleMeta().identifier(), DefaultModuleProvider.ModuleInformation::module)
                    .contains("test", Optional.of(module));
        }

        @Test
        @DisplayName("should register module that does not implement ArtModule")
        void shouldRegisterRandomModule() {

            assertThatCode(() -> provider.register(RandomModule.class)).doesNotThrowAnyException();
            AbstractObjectAssert<?, DefaultModuleProvider.ModuleInformation> moduleAssert = assertThat(provider.modules)
                    .hasSize(1)
                    .extractingByKey(RandomModule.class)
                    .isNotNull();

            moduleAssert
                    .extracting(DefaultModuleProvider.ModuleInformation::moduleMeta)
                    .extracting(moduleMeta -> moduleMeta.identifier(), moduleMeta -> moduleMeta.moduleClass())
                    .contains("foobar", RandomModule.class);
        }

        @Test
        @DisplayName("should create new instance of module class")
        void shouldCreateNewInstanceOfModule() {

            assertThatCode(() -> provider.register(TestModule.class)).doesNotThrowAnyException();
            DefaultModuleProvider.ModuleInformation information = provider.modules.get(TestModule.class);
            assertThat(information.module())
                    .isNotEmpty().get()
                    .extracting("created")
                    .isEqualTo(true);
        }
    }

    @ArtModule(identifier = "test")
    static class TestModule {

        private boolean created = false;

        public TestModule() {

            created = true;
        }

        @OnEnable
        public void onEnable(Configuration configuration) {

        }

        @OnDisable
        public void onDisable(Configuration configuration) {

        }
    }

    @ArtModule(identifier = "test")
    static class DuplicateModule {

        @OnEnable
        public void onEnable(Configuration configuration) {

        }

        @OnDisable
        public void onDisable(Configuration configuration) {

        }
    }

    static class MissingAnnotationModule {
        @OnEnable
        public void onEnable(Configuration configuration) {

        }

        @OnDisable
        public void onDisable(Configuration configuration) {

        }
    }

    @ArtModule(identifier = "foo", dependencies = "bar")
    static class FooModule {
        @OnEnable
        public void onEnable(Configuration configuration) {

        }

        @OnDisable
        public void onDisable(Configuration configuration) {

        }
    }

    @ArtModule(identifier = "bar")
    static class BarModule {
        @OnEnable
        public void onEnable(Configuration configuration) {

        }

        @OnDisable
        public void onDisable(Configuration configuration) {

        }
    }

    @ArtModule(identifier = "module 1", dependencies = {"module 2", "foo"})
    static class Module1 {

        @OnEnable
        public void onEnable(Configuration configuration) {

        }

        @OnDisable
        public void onDisable(Configuration configuration) {

        }
    }

    @ArtModule(identifier = "module 2", dependencies = "module 3")
    static class Module2 {

        @OnEnable
        public void onEnable(Configuration configuration) {

        }

        @OnDisable
        public void onDisable(Configuration configuration) {

        }
    }

    @ArtModule(identifier = "module 3", description = "module 1")
    static class Module3 {

        @OnEnable
        public void onEnable(Configuration configuration) {

        }

        @OnDisable
        public void onDisable(Configuration configuration) {

        }
    }

    @ArtModule(identifier = "foobar")
    static class RandomModule {

    }
}