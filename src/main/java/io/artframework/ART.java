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
import io.artframework.annotations.OnBootstrap;
import io.artframework.annotations.OnEnable;
import io.artframework.annotations.OnLoad;
import io.artframework.events.Event;
import io.artframework.events.EventManager;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public final class ART {

    private ART() {}

    @Getter
    private static Scope globalScope = Scope.defaultScope();

    static void globalScope(Scope scope) {
        globalScope = scope;
    }

    /**
     * Initializes the art-framework using the given bootstrap module.
     * <p>
     * Bootstrapping is only required by the root module that implements and ships the art-framework.
     * Normal modules should not use this bootstrap method, but instead tag their class with @{@link ArtModule}
     * and use the respective tagged methods ({@link OnBootstrap}, {@link OnLoad} and{@link OnEnable}) to load themselves into the scope.
     * <p>
     * By default the module will be bootstrapped into its own scope.
     * Use the {@link #bootstrap(BootstrapModule, boolean)} method with true to bootstrap the global scope.
     *
     * @param module the root module that is used to bootstrap the art-framework
     * @return the scope created by the bootstrap process
     * @throws BootstrapException if an error occurred while bootstrapping the module
     */
    public static Scope bootstrap(@NonNull BootstrapModule module) throws BootstrapException {

        return bootstrap(module, false);
    }

    /**
     * Initializes the art-framework using the given bootstrap module.
     * <p>
     * Bootstrapping is only required by the root module that implements and ships the art-framework.
     * Normal modules should not use this bootstrap method, but instead tag their class with @{@link ArtModule}
     * and use the respective tagged methods ({@link OnBootstrap}, {@link OnLoad} and{@link OnEnable}) to load themselves into the scope.
     * <p>
     * Set the boolean switch to true to set the resulting scope of the bootstrap process as the global scope.
     *
     * @param module the root module that is used to bootstrap the art-framework
     * @param global true if the result scope of the bootstrap process should be set as the global scope
     * @return the scope created by the bootstrap process
     * @throws BootstrapException if an error occurred while bootstrapping the module
     */
    public static Scope bootstrap(@NonNull BootstrapModule module, boolean global) throws BootstrapException {

        try {
            BootstrapScope bootstrapScope = BootstrapScope.of(module);
            module.enable(bootstrapScope);
            Scope scope = bootstrapScope.bootstrap();

            if (global) globalScope(scope);

            return scope;
        } catch (Exception e) {
            throw new BootstrapException(e);
        }
    }

    public static <TEvent extends Event> TEvent callEvent(TEvent event) {
        return EventManager.callEvent(event);
    }
}
