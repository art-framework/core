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

package net.silthus.art.api;

import net.silthus.art.ART;
import net.silthus.art.ArtModuleDescription;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.target.Target;

import java.util.function.Consumer;

/**
 * A {@link Requirement} can be used to filter {@link Action}s, {@link Trigger}
 * or be used inside other plugins for filtering.
 * <br>
 * Register your requirements by calling {@link ART#register(ArtModuleDescription, Consumer)}.
 * This will wrap the {@link Requirement} into a {@link RequirementContext} and bundle it with the configured options.
 * <br>
 * A requirement will only be used for filtering if the target type matches with the object that is being filtered.
 * <br>
 * For example: an {@link Action} that is targeting players can only be filtered by requirements
 * that also target players.
 *
 * @param <TTarget> target type this requirement applies too. Defines the filtering scope.
 * @param <TConfig> config type of the requirement
 * @see RequirementContext
 * @see Action
 * @see Trigger
 */
@FunctionalInterface
public interface Requirement<TTarget, TConfig> extends ArtObject {

    /**
     * Tests if this requirement should filter {@link Action}s or {@link Trigger}s for the given target.
     * Return true for the check to pass and not to filter out the action.
     * Return false if the check fails and the actions should be filtered.
     *
     * @param target  target to check against
     * @param context {@link RequirementContext} that holds the config and context of the check
     * @return false if the check fails and the filter should be applied or true if all checks pass and no filtering should be applied.
     */
    boolean test(Target<TTarget> target, RequirementContext<TTarget, TConfig> context);
}
