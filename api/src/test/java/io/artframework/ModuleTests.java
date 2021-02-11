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

package io.artframework;

import io.artframework.integration.BootstrapTestModule;
import io.artframework.integration.data.Block;
import io.artframework.integration.data.Entity;
import io.artframework.integration.data.Player;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ModuleTests {

    private BootstrapTestModule module;
    private BootstrapScope bootstrapScope;

    @BeforeEach
    void setUp() {
        ART.globalScope(Scope.defaultScope());
        module = spy(new BootstrapTestModule());
        bootstrapScope = spy(BootstrapScope.of(module));
    }

    @AfterEach
    void tearDown() {
        BootstrapTestModule.EnableModule.called = false;
        BootstrapTestModule.BootstrapModule.called = false;
        BootstrapTestModule.LoadModule.called = false;
        BootstrapTestModule.ErrorBootstrapModule.called = false;
        BootstrapTestModule.ErrorLoadModule.called = false;
    }

    @SneakyThrows
    @Test
    @DisplayName("should bootstrap module")
    void shouldBootstrapModule() {

        ART.bootstrap(bootstrapScope);

        verify(module, times(1)).onBootstrap(bootstrapScope);
    }

    @Test
    @SneakyThrows
    @DisplayName("should provide instance of custom provider")
    void shouldProvideInstanceOfCustomProvider() {

        Scope scope = ART.bootstrap(bootstrapScope);

        assertThat(scope.get(BootstrapTestModule.CustomProvider.class))
                .isNotNull();
    }

    @SneakyThrows
    @Test
    @DisplayName("should enable module")
    void shouldEnableModule() {

        ART.bootstrap(bootstrapScope).enableAll();

        verify(module, times(1)).onEnable(any(Scope.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("should load and enable all provided modules")
    void shouldLoadAllProvidedModules() {

        Scope scope = ART.bootstrap(bootstrapScope).enableAll();

        assertThat(scope.configuration().modules().all())
                .extracting(ModuleMeta::identifier)
                .contains("test", "bootstrap", "enable", "load");

        assertThat(BootstrapTestModule.BootstrapModule.called).isTrue();
        assertThat(BootstrapTestModule.EnableModule.called).isTrue();
        assertThat(BootstrapTestModule.LoadModule.called).isTrue();
    }

    @SneakyThrows
    @Test
    @DisplayName("should not call other module methods if an error occured")
    void shouldNotCallMethodsAfterError() {

        ART.bootstrap(bootstrapScope);

        assertThat(BootstrapTestModule.ErrorLoadModule.called).isFalse();
        assertThat(BootstrapTestModule.ErrorBootstrapModule.called).isFalse();
    }

    @SneakyThrows
    @Test
    @DisplayName("should find and register all art in sub packages")
    void shouldFindAndRegisterAllArtInSubPackages() {

        bootstrapScope.settings().autoRegisterAllArt(true);
        Scope scope = ART.bootstrap(bootstrapScope).enableAll();

        assertThat(scope.configuration().actions().all())
                .hasSizeGreaterThanOrEqualTo(2)
                .containsKeys("damage", "text");

        assertThat(scope.configuration().requirements().all())
                .hasSizeGreaterThanOrEqualTo(2)
                .containsKeys("health", "name");

        assertThat(scope.configuration().trigger().all())
                .hasSizeGreaterThanOrEqualTo(2)
                .containsKeys("move", "damage");

        assertThat(scope.configuration().targets().all())
                .hasSizeGreaterThanOrEqualTo(3)
                .contains(Block.class, Entity.class, Player.class);
    }

    @SneakyThrows
    @Test
    @DisplayName("should not register art that has autoRegister=false")
    void shouldNotRegisterArtWithAutoRegisterFalse() {

        Scope scope = ART.bootstrap(bootstrapScope);

        assertThat(scope.configuration().actions().all())
                .doesNotContainKey("no-autoregister");
    }
}
