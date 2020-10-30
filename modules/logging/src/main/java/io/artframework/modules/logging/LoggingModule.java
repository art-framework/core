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

package io.artframework.modules.logging;

import io.artframework.ExecutionContext;
import io.artframework.Scope;
import io.artframework.annotations.*;
import io.artframework.events.*;
import lombok.extern.java.Log;

import java.util.stream.Collectors;

@ArtModule("art:logging")
@Log(topic = "art:debug")
public class LoggingModule implements EventListener {

    @ConfigOption boolean debug = false;

    @OnEnable
    public void onEnable(Scope scope) {
        if (debug) {
            scope.configuration().events().register(this);
        }
    }

    @OnDisable
    public void onDisable(Scope scope) {
        scope.configuration().events().unregister(this);
    }

    @OnReload
    public void onReload(Scope scope) {
        if (debug) {
            scope.configuration().events().register(this);
        } else {
            scope.configuration().events().unregister(this);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PreActionExecution(PreActionExecutionEvent<?> event) {
        logArtEvent("[ACTION][PRE-EXEC]", event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void ActionExecution(ActionExecutionEvent<?> event) {
        logArtEvent("[ACTION][EXECUTION]", event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void ActionExecuted(ActionExecutedEvent<?> event) {
        logArtEvent("[ACTION][EXECUTED]", event);
    }

    private void logArtEvent(String prefix, ExecutionContextEvent<?, ?> event) {
        String cancelled = "";
        if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
            cancelled = "[CANCELLED]";
        }
        log.info(prefix + cancelled + " "
                + event.getArtObject().identifier() + " - "
                + toString(event.getExecutionContext()));
    }

    private String toString(ExecutionContext<?> context) {
        return "targets=[" +
                context.targets().stream().map(Object::toString).collect(Collectors.joining(";")) +
                "] " +
                "current_context=[" +
                context.current().toString() +
                "]";
    }
}
