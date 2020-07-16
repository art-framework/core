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

import lombok.NonNull;
import net.silthus.art.conf.ActionConfig;
import net.silthus.art.impl.DefaultActionContext;

/**
 * The <pre>ActionContext</pre> wraps the actual {@link Action} and handles
 * the execution logic of the action.
 *
 * @param <TTarget> type of the target
 */
public interface ActionContext<TTarget> extends Action<TTarget>, ArtObjectContext<Action<TTarget>>, RequirementHolder, ActionHolder {

    static <TTarget> ActionContext<TTarget> of(
            @NonNull Configuration configuration,
            @NonNull ArtObjectInformation<Action<TTarget>> information,
            @NonNull Action<TTarget> action,
            @NonNull ActionConfig config
    ) {
        return new DefaultActionContext<>(configuration, information, action, config);
    }

    /**
     * Gets the config used by this {@link ActionContext}.
     *
     * @return config of this context
     */
    ActionConfig getConfig();
}
