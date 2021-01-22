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

import io.artframework.annotations.OnBootstrap;
import io.artframework.conf.Settings;
import io.artframework.impl.DefaultScope;

import java.util.Collection;
import java.util.function.Consumer;

public interface Scope extends DataProvider {

    static Scope defaultScope() {

        return new DefaultScope();
    }

    static Scope of(Consumer<Configuration.ConfigurationBuilder> config) {
        return new DefaultScope(config);
    }

    static Scope of(Configuration configuration) {
        return new DefaultScope(configuration);
    }

    /**
     * Gets the current settings of this scope.
     * <p>
     * The scope is initialized with its settings upon creation and will read them from a config file.
     * The settings can be modified by any service during the lifetime of the scope.
     *
     * @return the current scope settings
     */
    Settings settings();

    /**
     * The current configuration of this scope.
     * <p>
     * The configuration is never null but may change during the lifetime of the scope.
     * Directly access the configuration from the scope when you need it and do not cache it in a variable.
     * <p>
     * The configuration may change when the scope is being bootstrapped, but should stay the same after
     * bootstrapping has finished.
     *
     * @return the current configuration of this scope
     */
    Configuration configuration();

    /**
     * Tries to find a provider for the registered provider class and returns its instance.
     * <p>By default all providers are singletons that can be registered {@link OnBootstrap}
     * with the {@link BootstrapScope}.
     *
     * @param providerClass the class of the provider that should be retrieved
     * @param <TProvider> the type of the provider
     * @return the provider if it is registered else null
     */
    <TProvider extends Provider> TProvider get(Class<TProvider> providerClass);

    /**
     * Gets the storage provider of this scope.
     * <p>
     * Use the {@link StorageProvider} to store persistent data for your module or targets.
     * <p>
     * This is just a shortcut to {@link #configuration()#store()}.
     *
     * @return the storage provider
     * @see StorageProvider
     */
    default StorageProvider store() {
        return configuration().storage();
    }

    /**
     * Use the {@link ArtProvider} to register your art objects,
     * like actions, requirements and trigger.
     *
     * @return the art provider to register art objects
     */
    default ArtProvider register() {

        return configuration().art();
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
    ArtContext load(String key, Collection<String> list) throws ParseException;

    /**
     * Loads the given string list and tries to parse each line
     * into a valid art object.
     * <p>Use the resulting {@link ArtContext} to {@link ArtContext#execute(Object...)},
     * {@link ArtContext#test(Object)} or listen {@link ArtContext#onTrigger(Class, TriggerListener)}.
     *
     * @param list the list of valid art that is parsed into an art context
     * @return the parsed art context
     */
    ArtContext load(Collection<String> list) throws ParseException;

    /**
     * Starts a new trigger execution for the given trigger class.
     * <p>Use the returned execution builder to add targets and additional
     * config parameters to the trigger execution. Whe done call {@link TriggerExecution#execute()}.
     *
     * @param triggerClass the class of the trigger
     * @param <TTrigger> the type of the trigger
     * @return a new trigger execution builder
     */
    default <TTrigger extends Trigger> TriggerExecution<TTrigger> trigger(Class<TTrigger> triggerClass) {

        return TriggerExecution.of(this, triggerClass);
    }
}
