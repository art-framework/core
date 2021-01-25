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

import io.artframework.conf.TriggerConfig;
import io.artframework.impl.DefaultTriggerContext;
import lombok.NonNull;

import java.util.Set;

public interface TriggerContext extends ArtObjectContext<Trigger>, ActionHolder, RequirementHolder {

    /**
     * Creates a new default trigger context instance with the provided parameters.
     *
     * @param scope the scope of the trigger
     * @param config the config of the trigger
     * @param factory the factory used to create the trigger
     * @param artObjectConfig the config passed to the factory when creating the trigger
     * @return the created trigger context
     */
    static TriggerContext of(
            @NonNull Scope scope,
            @NonNull TriggerConfig config,
            @NonNull TriggerFactory factory,
            @NonNull ConfigMap artObjectConfig
            ) {
        return new DefaultTriggerContext(scope, config, factory, artObjectConfig);
    }

    static TriggerContext of(
            @NonNull Scope scope,
            @NonNull ArtObjectMeta<Trigger> meta,
            @NonNull Trigger trigger,
            @NonNull TriggerConfig config
    ) {

        return new DefaultTriggerContext(scope, meta, trigger, config);
    }

    /**
     * Gets the {@link TriggerConfig} used in this {@link TriggerContext}.
     *
     * @return trigger config
     */
    TriggerConfig config();

    /**
     * Enables this trigger context to start listening on trigger events
     * for the configured trigger type.
     * <p>Nothing will happen until {@code enable()} is called.
     * <p>Make sure to call {@link #disable()} when the trigger is no longer
     * in use to allow garbage collection to run.
     */
    void enable();

    /**
     * Disables this trigger and stops listening to all events for the given trigger type.
     * <p>Make sure to call this method when the trigger context is no longer required.
     */
    void disable();

    /**
     * Fires this trigger and informs all listeners about its executing
     * if the given predicate and target type matches.
     *
     * @param targets the targets that triggered this trigger
     */
    void trigger(final Target<?>[] targets);

    /**
     * Fires this trigger and informs all listeners about its executing
     * if the given predicate and target type matches.
     *
     * @param targets the targets that triggered this trigger
     */
    void trigger(ExecutionContext<TriggerContext> context, final Target<?>[] targets);

    /**
     * Registers the given {@link TriggerListener} to listen for events
     * fired by this trigger.
     *
     * @param listener trigger listener to register
     * @see Set#add(Object)
     */
    <TTarget> TriggerContext addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener);

    /**
     * Registers the given trigger listener to listen on all events fired by this trigger.
     *
     * @param listener the listener to register
     */
    TriggerContext addListener(TriggerListener<Object> listener);

    /**
     * Unregisters the given listener from listening to this trigger.
     *
     * @param listener trigger listener to unregister
     */
    <TTarget> TriggerContext removeListener(TriggerListener<TTarget> listener);
}
