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
import io.artframework.impl.DefaultScope;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Collection;

@Accessors(fluent = true)
public final class ART {

    private ART() {}

    private static Scope globalScope = new DefaultScope();

    /**
     * Gets the global art scope, which is the main entrypoint for the art-framework.
     * <p>The global scope is set when the bootstrap module is initialized
     * and can be safely used as it is the only scope in most cases.
     * <p>All methods in this static class reference this global scope.
     * <p>Use the {@link #bootstrap(BootstrapScope)} method to register your boostrap module
     * and register it as the global scope.
     *
     * @return the global scope that was registered when bootstrapping
     */
    public static Scope scope() {

        return globalScope;
    }

    static void scope(@NonNull Scope scope) {
        globalScope = scope;
    }

    /**
     * Initializes the art-framework using the given bootstrap scope.
     * <p>Make sure to call the {@link BootstrapPhase#loadAll()} and {@link BootstrapPhase#enableAll()} methods
     * to load and enable all modules contained within the scope.
     * <p>Bootstrapping is only required by the root module that implements and ships the art-framework.
     * Normal modules should not use this bootstrap method, but instead tag their class with @{@link ArtModule}
     * and use the respective tagged methods ({@link OnBootstrap}, {@link OnLoad} and{@link OnEnable}) to load themselves into the scope.
     * <p>By default the module will be bootstrapped and set as the global scope.
     * Use the {@link #bootstrap(BootstrapScope, boolean)} method with false to bootstrap without setting the global scope.
     *
     * @param bootstrapScope the bootstrap scope containing the bootstrap module used to start the bootstrap process
     * @return the bootstrapping phase created for the given scope
     * @throws BootstrapException if an error occurred while bootstrapping the module
     */
    public static BootstrapPhase bootstrap(@NonNull BootstrapScope bootstrapScope) throws BootstrapException {

        return bootstrap(bootstrapScope, true);
    }

    /**
     * Initializes the art-framework using the given bootstrap scope.
     * <p>Make sure to call the {@link BootstrapPhase#loadAll()} and {@link BootstrapPhase#enableAll()} methods
     * to load and enable all modules contained within the scope.
     * <p>Bootstrapping is only required by the root module that implements and ships the art-framework.
     * Normal modules should not use this bootstrap method, but instead tag their class with @{@link ArtModule}
     * and use the respective tagged methods ({@link OnBootstrap}, {@link OnLoad} and{@link OnEnable}) to load themselves into the scope.
     * <p>Set the boolean switch to true to set the resulting scope of the bootstrap process as the global scope.
     *
     * @param bootstrapScope the bootstrap scope containing the bootstrap module used to start the bootstrap process
     * @param global true if the result scope of the bootstrap process should be set as the global scope
     * @return the bootstrapping phase created for the given scope
     * @throws BootstrapException if an error occurred while bootstrapping the module
     */
    public static BootstrapPhase bootstrap(@NonNull BootstrapScope bootstrapScope, boolean global) throws BootstrapException {

        try {
            BootstrapPhase bootstrap = bootstrapScope.bootstrap();

            if (global) scope(bootstrapScope);

            return bootstrap;
        } catch (Exception e) {
            throw new BootstrapException(e);
        }
    }

    /**
     * Loads the given string list and tries to parse each line into a valid art object.
     * <p>Uses the provided storage key to store the data of the context and reference it in future uses.
     * Make sure to pass in the same storage key for all loads that need to persist data across sessions.
     * <p>Use the resulting {@link ArtContext} to {@link ArtContext#execute(Object...)},
     * {@link ArtContext#test(Object)} or listen {@link ArtContext#onTrigger(Class, TriggerListener)}.
     *
     * @param key the unique storage key for this provided input
     * @param list the list of valid art that is parsed into an art context
     * @return the parsed art context
     * @throws ParseException if the parse operation of the input fails
     * @see Scope#load(String, Collection)
     */
    public static ArtContext load(String key, Collection<String> list) throws ParseException {

        return scope().load(key, list);
    }

    /**
     * Loads the given string list and tries to parse each line
     * into a valid art object.
     * <p>Use the resulting {@link ArtContext} to {@link ArtContext#execute(Object...)},
     * {@link ArtContext#test(Object)} or listen {@link ArtContext#onTrigger(Class, TriggerListener)}.
     *
     * @param list the list of valid art that is parsed into an art context
     * @return the parsed art context
     * @throws ParseException if the parse operation of the input fails
     * @see Scope#load(Collection)
     */
    public static ArtContext load(Collection<String> list) throws ParseException {

        return scope().load(list);
    }

    /**
     * Starts a new trigger execution on the global scope for the given trigger class.
     * <p>Use the returned execution builder to add targets and additional
     * config parameters to the trigger execution. Whe done call {@link TriggerExecution#execute()}.
     *
     * @param triggerClass the class of the trigger
     * @param <TTrigger> the type of the trigger
     * @return a new trigger execution builder
     */
    public static <TTrigger extends Trigger> TriggerExecution<TTrigger> trigger(Class<TTrigger> triggerClass) {

        return scope().trigger(triggerClass);
    }
}
