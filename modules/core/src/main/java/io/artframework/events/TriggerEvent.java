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

package io.artframework.events;

import io.artframework.TriggerTarget;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TriggerEvent extends Event implements Cancellable {

    private final String identifier;
    private final TriggerTarget<?>[] targets;
    @Setter
    private boolean cancelled;

    public TriggerEvent(String identifier, TriggerTarget<?>... targets) {
        this.identifier = identifier;
        this.targets = targets;
    }

    ///
    /// Required Event internal HandlerList
    ///

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    protected HandlerList getHandlers() {
        return handlerList;
    }
}
