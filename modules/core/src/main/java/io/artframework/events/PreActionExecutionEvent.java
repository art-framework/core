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

import io.artframework.Action;
import io.artframework.ActionContext;
import io.artframework.ArtObject;
import io.artframework.ExecutionContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PreActionExecutionEvent<TTarget> extends ExecutionContextEvent<Action<TTarget>, ActionContext<TTarget>> implements Cancellable {

    private boolean cancelled;

    public PreActionExecutionEvent(ArtObject artObject, ExecutionContext<ActionContext<TTarget>> executionContext) {
        super(artObject, executionContext);
    }

    ///
    /// Required Event internal HandlerList
    ///

    private static HandlerList handlers = new HandlerList();

    @Override
    protected HandlerList getHandlers() {
        return handlers;
    }
}
