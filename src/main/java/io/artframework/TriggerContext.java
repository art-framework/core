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

public interface TriggerContext extends ArtObjectContext<Trigger>, ActionHolder, RequirementHolder, AutoCloseable {

    static TriggerContext of(
            @NonNull Configuration configuration,
            @NonNull Options<Trigger> information,
            @NonNull TriggerConfig config
    ) {
        return new DefaultTriggerContext(configuration, information, config);
    }

    /**
     * Gets the {@link TriggerConfig} used in this {@link TriggerContext}.
     *
     * @return trigger config
     */
    TriggerConfig config();

    /**
     * Fires this trigger and informs all listeners about its executing
     * if the given predicate and target type matches.
     *
     * @param targets the targets that triggered this trigger
     * @param context the execution context of this trigger
     */
    void trigger(final TriggerTarget<?>[] targets, final ExecutionContext<TriggerContext> context);

    /**
     * Registers the given {@link TriggerListener} to listen for events
     * fired by this trigger.
     *
     * @param listener trigger listener to register
     * @see Set#add(Object)
     */
    <TTarget> void addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener);

    void addListener(TriggerListener<Object> listener);

    /**
     * Unregisters the given listener from listening to this trigger.
     *
     * @param listener trigger listener to unregister
     */
    <TTarget> void removeListener(TriggerListener<TTarget> listener);
}
