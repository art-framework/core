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

package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.AbstractProvider;
import net.silthus.art.Configuration;
import net.silthus.art.EventProvider;
import net.silthus.art.events.ArtEventListener;
import net.silthus.art.events.EventManager;

public class DefaultEventProvider extends AbstractProvider implements EventProvider {

    public DefaultEventProvider(@NonNull Configuration configuration) {
        super(configuration);
    }

    @Override
    public EventProvider register(ArtEventListener listener) {
        EventManager.registerListeners(listener);
        return this;
    }

    @Override
    public EventProvider unregister(ArtEventListener listener) {
        EventManager.unregisterListeners(listener);
        return this;
    }

    @Override
    public EventProvider unregisterAll() {
        EventManager.unregisterAll();
        return this;
    }
}
