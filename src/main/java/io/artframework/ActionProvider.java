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

import io.artframework.impl.DefaultActionProvider;

public interface ActionProvider extends ArtProvider, FactoryProvider<ActionFactory<?>> {

    static ActionProvider of(Configuration configuration) {
        return new DefaultActionProvider(configuration);
    }

    ActionProvider add(Options<Action<?>> actionInformation);

    ActionProvider add(Class<? extends Action<?>> actionClass);

    ActionProvider add(String identifier, GenericAction action);

    <TTarget> ActionProvider add(String identifier, Class<TTarget> targetClass, Action<TTarget> action);

    <TAction extends Action<TTarget>, TTarget> ActionProvider add(Class<TAction> actionClass, ArtObjectProvider<TAction> action);
}
