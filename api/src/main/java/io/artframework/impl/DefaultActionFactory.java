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

package io.artframework.impl;

import io.artframework.AbstractFactory;
import io.artframework.Action;
import io.artframework.ActionContext;
import io.artframework.ActionFactory;
import io.artframework.ArtObjectMeta;
import io.artframework.Scope;
import io.artframework.conf.ActionConfig;
import io.artframework.conf.ContextConfig;

public class DefaultActionFactory<TTarget> extends AbstractFactory<ActionContext<TTarget>, Action<TTarget>> implements ActionFactory<TTarget> {

    public DefaultActionFactory(Scope scope, ArtObjectMeta<Action<TTarget>> information) {
        super(scope, information);
    }

    @Override
    public ActionContext<TTarget> createContext(ContextConfig config) {

        return ActionContext.of(
                scope(),
                ActionConfig.of(scope(), config.contextConfig()),
                this,
                config.artObjectConfig()
        );
    }
}
