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

package net.silthus.art.api.trigger;

import lombok.EqualsAndHashCode;
import net.silthus.art.api.ArtContext;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@EqualsAndHashCode(callSuper = true)
public class TriggerContext<TConfig> extends ArtContext<Object, TConfig, TriggerConfig<TConfig>> {

    private final Set<TriggerListener> listeners = new HashSet<>();

    public TriggerContext(TriggerConfig<TConfig> config) {
        super(Object.class, config);
    }

    /**
     * Registers the given {@link TriggerListener} to listen for events
     * fired by this trigger.
     * Will return false if the listener is already registered.
     *
     * @param listener trigger listener to register
     * @return false if listener was already registered
     * @see Set#add(Object)
     */
    boolean registerListener(TriggerListener listener) {
        return listeners.add(listener);
    }

    /**
     * Unregisters the given listener from listening to this trigger.
     * Returns false if the listener was never registered.
     *
     * @param listener trigger listener to unregister
     * @return false if listener was never registered
     */
    boolean unregisterListener(TriggerListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Fires this trigger and informs all listeners about its executing
     * if the given predicate and target type matches.
     *
     * @param target target that fired the trigger
     * @param predicate predicate to check before informing all listeners
     * @param <TTarget> target type
     */
    <TTarget> void trigger(Target<TTarget> target, Predicate<TriggerContext<TConfig>> predicate) {
        if (predicate.test(this)) {
            listeners.forEach(listener -> listener.onTrigger(target));
        }
    }
}
