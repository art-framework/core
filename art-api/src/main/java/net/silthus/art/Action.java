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

/**
 * Defines an action that can get executed if the right {@link Trigger} was called.
 * The implementing class must have a public parameterless constructor or you need
 * to provide a {@link ArtObjectProvider} for the type and register it with the
 * {@link ArtProvider#action(Class, ArtObjectProvider)}.
 * <br>
 * The {@link Trigger} source and action target must match or the action will not be executed.
 * Make the {@link TTarget} as broad as possible to allow the action to be executed by as many triggers as possible.
 * <br>
 * You can use any field in this class as a config option as long as you annotate it with @{@link ConfigOption}.
 *
 * @param <TTarget> the target this action applies to.
 *                  This could be a player, entity or anything as long as there is a trigger for it.
 */
@FunctionalInterface
public interface Action<TTarget> extends ArtObject {

    /**
     * Called when the action is executed.
     * The action should handle the pure execution and no filtering.
     * All filtering is done beforehand and by the means of attached {@link Requirement}s.
     * Use the @{@link ConfigOption} annotation on fields of this class
     * to provide configuration options for users of this action.
     * <br>
     * Make sure to annotate this {@link Action} with a @{@link ArtOptions}
     * and optionally provide a config class and implement {@link Configurable}.
     *
     * @param context context of this action.
     *                Use the {@link ExecutionContext} to retrieve the config, target
     *                and a lot of additional information about the execution.
     */
    void execute(ExecutionContext<TTarget, ActionContext<TTarget>> context);
}
