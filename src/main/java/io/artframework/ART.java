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

import io.artframework.events.Event;
import io.artframework.events.EventManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public final class ART {

    private ART() {}

    @Getter
    private static final Scope globalScope = Scope.defaultScope();

    public static Scope bootstrap(Object module) {
        return bootstrap(globalScope(), module);
    }

    public static Scope bootstrap(Scope scope, Object module) {
        try {
            scope.configuration().modules().enable(module);
            return scope;
        } catch (ModuleRegistrationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <TEvent extends Event> TEvent callEvent(TEvent event) {
        return EventManager.callEvent(event);
    }
}
