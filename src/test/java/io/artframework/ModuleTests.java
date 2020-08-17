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

import io.artframework.annotations.ArtModule;
import io.artframework.annotations.OnLoad;
import io.artframework.annotations.OnReload;
import io.artframework.integration.BootstrapTestModule;
import io.artframework.integration.data.Block;
import io.artframework.integration.data.Entity;
import io.artframework.integration.data.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ModuleTests {

    private BootstrapTestModule module;

    @BeforeEach
    void setUp() {
        ART.globalScope(Scope.defaultScope());
        module = spy(new BootstrapTestModule());
    }

    @Test
    @DisplayName("should bootstrap module")
    void shouldBootstrapModule() {

        Scope scope = ART.bootstrap(module);

        verify(module, times(1)).onBootstrap(scope);
    }

    @Test
    @DisplayName("should enable module")
    void shouldEnableModule() {

        Scope scope = ART.bootstrap(module);

        verify(module, times(1)).enable(scope);
    }

    @Test
    @DisplayName("should reload module if configuration changes")
    void shouldReloadModule() {

        Scope scope = ART.bootstrap(module);

        verify(module, never()).onReload(any());

        scope.update(configurationBuilder -> configurationBuilder.storage(null));
        verify(module, times(1)).onReload(scope);
    }

    @Test
    @DisplayName("should not reload the module before bootstrapping finished")
    void shouldNotReloadModuleBeforeBootstrapFinished() {

        BootstrapModule module = spy(new BootstrapModule());
        ART.bootstrap(module);

        verify(module, never()).reload();
    }

    @Test
    @DisplayName("should find and register all art in sub packages")
    void shouldFindAndRegisterAllArtInSubPackages() {

        Scope scope = ART.bootstrap(module);

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

    @ArtModule
    public static class BootstrapModule {

        @OnLoad
        public void onBootstrap(Scope scope) {
            scope.update(configurationBuilder -> configurationBuilder.storage(null));
        }

        @OnReload
        public void reload() {

        }
    }
}
