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

package net.silthus.art;

import net.silthus.art.impl.DefaultTriggerProvider;

import java.util.Set;
import java.util.function.Predicate;

// TODO: javadoc
public interface TriggerProvider extends ArtProvider {

    static TriggerProvider of(Configuration configuration) {
        return new DefaultTriggerProvider(configuration);
    }

    TriggerProvider add(ArtInformation<Trigger> triggerInformation);

    TriggerProvider add(Class<? extends Trigger> triggerClass);

    <TTrigger extends Trigger> TriggerProvider add(Class<? extends TTrigger> triggerClass, ArtObjectProvider<TTrigger> trigger);

    // TODO: javadoc
    void trigger(String identifier, Predicate<ExecutionContext<?, TriggerContext>> predicate, Target<?>... targets);

    void trigger(String identifier, Target<?>... targets);

    /**
     * Registers the given {@link TriggerListener} to listen for events
     * fired by this trigger.
     *
     * @param listener trigger listener to register
     * @see Set#add(Object)
     */
    <TTarget> void addListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener);

    /**
     * Unregisters the given listener from listening to this trigger.
     *
     * @param listener trigger listener to unregister
     */
    <TTarget> void removeListener(TriggerListener<TTarget> listener);
}
