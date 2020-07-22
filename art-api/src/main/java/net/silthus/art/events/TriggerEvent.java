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

package net.silthus.art.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.ExecutionContext;
import net.silthus.art.Target;
import net.silthus.art.TriggerContext;

import java.util.function.Predicate;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TriggerEvent extends Event {

    private final String identifier;
    private final Predicate<ExecutionContext<?, TriggerContext>> predicate;
    private final Target<?>[] targets;

    public TriggerEvent(String identifier, Predicate<ExecutionContext<?, TriggerContext>> predicate, Target<?>... targets) {
        this.identifier = identifier;
        this.predicate = predicate;
        this.targets = targets;
    }

    public TriggerEvent(String identifier, Target<?>... targets) {
        this.identifier = identifier;
        this.predicate = triggerContextExecutionContext -> true;
        this.targets = targets;
    }

    ///
    /// Required Event internal HandlerList
    ///

    private static final HandlerList handlers = new HandlerList();

    @Override
    protected HandlerList getHandlers() {
        return handlers;
    }
}
