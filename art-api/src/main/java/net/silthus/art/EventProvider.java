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

import net.silthus.art.events.ArtEventListener;
import net.silthus.art.impl.DefaultEventProvider;

/**
 * Use the {@link EventProvider} to provide one or
 * multiple {@link ArtEventListener}s that can act and listen upon various events.
 *
 * @see ArtEventListener
 */
public interface EventProvider extends Provider {

    static EventProvider of(Configuration configuration) {
        return new DefaultEventProvider(configuration);
    }

    /**
     * Adds a new {@link ArtEventListener} that will listen to any
     * lifecycle events configured as method overrides.
     *
     * @param listener The {@link ArtEventListener} to register
     * @return this {@link EventProvider}
     */
    EventProvider register(ArtEventListener listener);

    /**
     * Removes the given {@link ArtEventListener} from the list
     * of listening lifecycle listeners.
     *
     * @param listener The {@link ArtEventListener} to remove
     * @return this {@link EventProvider}
     */
    EventProvider unregister(ArtEventListener listener);

    /**
     * Unregisters all registered {@link ArtEventListener}s.
     *
     * @return this {@link EventProvider}
     */
    EventProvider unregisterAll();
}
