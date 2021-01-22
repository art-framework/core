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

import io.artframework.impl.DefaultTriggerProvider;
import lombok.NonNull;

public interface TriggerProvider extends ArtProvider, FactoryProvider<TriggerFactory> {

    /**
     * Creates a new default implementation of this trigger provider using the given configuration.
     *
     * @param scope the configuration to use
     * @return a new default trigger provider using the given configuration
     */
    static TriggerProvider of(Scope scope) {
        return new DefaultTriggerProvider(scope);
    }

    /**
     * Registers a new trigger using the provided information.
     * <p>
     * This will call {@link ArtObjectMeta#initialize()} if the information is not initialized and will fail
     * silently if there are any exceptions.
     * Using {@link #add(Class)} or {@link #add(Class, ArtObjectProvider)} is preferred since it will handle all the dirty work for you.
     *
     * @param triggerInformation the information of the trigger you want to register
     * @return this trigger provider
     */
    TriggerProvider add(@NonNull ArtObjectMeta<Trigger> triggerInformation);

    /**
     * Registers a new trigger extracting the needed information from the given class.
     * <p>
     * This will scan the given class for any {@link io.artframework.annotations.ART} annotations and uses that
     * information to register the trigger contained within that class.
     * All methods that are not annotated will be ignored.
     * You can also annotate the class if you only have one trigger method.
     * <p>
     * It will not try instantiate the class or setup any listening for the trigger defined inside it.
     *
     * @param triggerClass the trigger class that should be used to extract the required information
     * @return this trigger provider
     */
    TriggerProvider add(Class<? extends Trigger> triggerClass);

    /**
     * Registers a new trigger with the given supplier as factory.
     * <p>
     * This will scan the given class for any {@link io.artframework.annotations.ART} annotations and uses that
     * information to register the trigger contained within that class.
     * All methods that are not annotated will be ignored.
     * You can also annotate the class if you only have one trigger method.
     * <p>
     * It will use the provided supplier for every instantiation of the trigger.
     * This may happen multiple times during the livecycle of the art-framework.
     * Make sure you implement the {@link AutoCloseable} interface if you need to release any resources.
     *
     * @param triggerClass the trigger class that should be used to extract the required information
     * @param supplier the supplier that will be used to create new instances of the trigger
     * @param <TTrigger> the type of the trigger
     * @return this trigger provider
     */
    <TTrigger extends Trigger> TriggerProvider add(Class<TTrigger> triggerClass, ArtObjectProvider<TTrigger> supplier);

    /**
     * Registers the given TriggerContext to listen for fired trigger events.
     * <p>Nothing will happen if the provided context is already registered.
     *
     * @param context the trigger context that starts listening
     */
    void register(TriggerContext context);

    /**
     * Unregisters the given trigger context from this provider stopping any trigger calls.
     * <p>Nothing will happen if the trigger context is not registered.
     *
     * @param context the trigger context that stops listening
     */
    void unregister(TriggerContext context);

    /**
     * Unregisters all currently registered trigger contexts.
     */
    void unregisterAll();

    /**
     * Executes the given trigger execution calling all registered trigger context listeners
     * for the trigger of the execution.
     *
     * @param execution the trigger execution that is executed
     * @param <TTrigger> the type of the trigger
     */
    <TTrigger extends Trigger> void execute(TriggerExecution<TTrigger> execution);
}
