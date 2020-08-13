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
     * Bootstraps the global scope with the given module initializing the art-framework.
     * <p>
     * Bootstrapping is only required by the root module that implements and ships the art-framework.
     * Normal modules should not use this bootstrap method, but instead tag their class with @{@link ArtModule}
     * and use the respective tagged methods to load themselves into the scope.
     * <p>
     * Only one module can ever bootstrap the global scope.
     * Use the {@link #bootstrap(Scope, Object)} method to use your own scope
     * or do not bootstrap your module at all and wait for it to be loaded by the bootstrap module.
     *
     * @param module the root module that is used to bootstrap the art-framework
     * @return the global scope used to bootstrap the module
     */
    public static Scope bootstrap(@NonNull Object module) {

        return bootstrap(globalScope(), module);
    }

    public static Scope bootstrap(@NonNull Scope scope, @NonNull Object module) {

        return scope.bootstrap(module);
    }

    public static <TEvent extends Event> TEvent callEvent(TEvent event) {
        return EventManager.callEvent(event);
    }
}
