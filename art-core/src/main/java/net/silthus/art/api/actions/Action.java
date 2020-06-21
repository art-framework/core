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

package net.silthus.art.api.actions;

import net.silthus.art.ActionContext;
import net.silthus.art.api.ArtObject;

/**
 * Defines an action that can get executed if the right {@link net.silthus.art.api.trigger.Trigger} was called.
 * The {@link net.silthus.art.api.trigger.Trigger} source and action target must match or the action will not be executed.
 * Make the {@link TTarget} as broad as possible to allow the action to be executed by as many triggers as possible.
 *
 * @param <TTarget> the target this action applies to.
 *                 This could be a player, entity or anything as long as there is a trigger for it.
 * @param <TConfig> the config that should be used by this action.
 *                 You can provide your own type safe configs or use generic implementations like the Bukkit ConfigurationSection.
 */
@FunctionalInterface
public interface Action<TTarget, TConfig> extends ArtObject {

    /**
     * Called when the action is executed.
     * The action should handle the pure execution and no filtering.
     * All filtering is done beforehand and by the means of attached {@link net.silthus.art.api.requirements.Requirement}s.
     * Use the config to provide configuration options for users of this action.
     * <br>
     * Make sure to annotate this {@link Action} with a @{@link net.silthus.art.api.annotations.Name} and
     * optionally @{@link net.silthus.art.api.annotations.Config} or it wont be loaded.
     *
     * @param target target to apply this action to.
     * @param context context of this action.
     *                Use the {@link ActionContext} to retrieve the config
     *                and additional information about the execution context of this action.
     */
    void execute(TTarget target, ActionContext<TTarget, TConfig> context);
}
