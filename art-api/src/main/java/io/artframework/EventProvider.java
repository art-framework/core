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

import io.artframework.events.EventListener;
import io.artframework.impl.DefaultEventProvider;

/**
 * Use the {@link EventProvider} to provide one or
 * multiple {@link EventListener}s that can act and listen upon various events.
 *
 * @see EventListener
 */
public interface EventProvider extends Provider {

    static EventProvider of(Configuration configuration) {
        return new DefaultEventProvider(configuration);
    }

    /**
     * Adds a new {@link EventListener} that will listen to any
     * lifecycle events configured as method overrides.
     *
     * @param listener The {@link EventListener} to register
     * @return this {@link EventProvider}
     */
    EventProvider register(EventListener listener);

    /**
     * Removes the given {@link EventListener} from the list
     * of listening lifecycle listeners.
     *
     * @param listener The {@link EventListener} to remove
     * @return this {@link EventProvider}
     */
    EventProvider unregister(EventListener listener);

    /**
     * Unregisters all registered {@link EventListener}s.
     *
     * @return this {@link EventProvider}
     */
    EventProvider unregisterAll();
}
