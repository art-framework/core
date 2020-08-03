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

import io.artframework.annotations.ART;
import io.artframework.impl.DefaultTriggerProvider;
import lombok.NonNull;

// TODO: javadoc
public interface TriggerProvider extends ArtProvider, FactoryProvider<TriggerFactory> {

    /**
     * Creates a new default implementation of this trigger provider using the given configuration.
     *
     * @param configuration the configuration to use
     * @return a new default trigger provider using the given configuration
     */
    static TriggerProvider of(Configuration configuration) {
        return new DefaultTriggerProvider(configuration);
    }

    /**
     * Registers a new trigger using the provided information.
     * <p>
     * This will call {@link Options#initialize()} if the information is not initialized and will fail
     * silently if there are any exceptions.
     * Using {@link #add(Class)} or {@link #add(Trigger)} is preferred since it will handle all the dirty work for you.
     *
     * @param triggerInformation the information of the trigger you want to register
     * @return this trigger provider
     */
    TriggerProvider add(@NonNull Options<Trigger> triggerInformation);

    /**
     * Registers a new trigger extracting the needed information from the given class.
     * <p>
     * This will scan the given class for any {@link io.artframework.annotations.ART} annotations and uses that
     * information to register the trigger contained within that class.
     * All methods that are not annotated will be ignored.
     * You can also annotate the class if you only have one trigger method.
     * <p>
     * It will not try instantiate the class or setup any listening for the trigger defined inside it.
     * <p>
     * You can also use the {@link #add(Trigger)} method as an alternative to this.
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
     * It will use the provided supplier for every instatiation of the trigger.
     * This may happen multiple times during the livecycle of the art-framework.
     * Make sure you implement the {@link AutoCloseable} interface if you need to release any resources.
     * <p>
     * You can also use the {@link #add(Trigger)} method as an alternative to this.
     *
     * @param triggerClass the trigger class that should be used to extract the required information
     * @param supplier the supplier that will be used to create new instances of the trigger
     * @param <TTrigger> the type of the trigger
     * @return this trigger provider
     */
    <TTrigger extends Trigger> TriggerProvider add(Class<TTrigger> triggerClass, ArtObjectProvider<TTrigger> supplier);

    /**
     * Registers a new trigger extracting the needed information from the given trigger instance.
     * <p>
     * This will scan the trigger object for any {@link ART} annotations and uses that
     * information to register the trigger contained within that class.
     * All methods that are not annotated will be ignored.
     * You can also annotate the class if you only have one trigger method.
     * <p>
     * You can also use the {@link #add(Class)} method as an alternative to this.
     *
     * @param trigger the trigger object that should be used to extract the required information
     * @return this trigger provider
     */
    TriggerProvider add(Trigger trigger);

    /**
     * Triggers the trigger that matches the given identifier or has an alias that matches it.
     * <p>
     * Will return a {@link TriggerResult} that contains all information about the execution
     * of the {@link Trigger} and all of its child executions and checks.
     * This may be in the future since trigger execution may be delayed.
     * <p>
     * The result will never be null even if there are not trigger with the given identifier.
     * A result that had no trigger will be empty instead. Use {@link TriggerResult#isEmpty()} to check that.
     *
     * @param identifier the identifier or alias of the trigger
     * @param targets the trigger targets that wrap a predicate
     * @return the result that contains the outcome of the trigger
     */
    CombinedResult trigger(String identifier, TriggerTarget<?>... targets);
}
