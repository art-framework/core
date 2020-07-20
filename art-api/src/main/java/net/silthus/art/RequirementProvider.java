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

import net.silthus.art.impl.DefaultRequirementProvider;

public interface RequirementProvider extends ArtProvider, ArtFactoryProvider<RequirementFactory<?>> {

    static RequirementProvider getDefault() {
        return new DefaultRequirementProvider(ART.configuration());
    }

    RequirementProvider add(ArtInformation<Requirement<?>> information);

    RequirementProvider add(Class<? extends Requirement<?>> requirementClass);

    RequirementProvider add(String identifier, GenericRequirement requirement);

    <TTarget> RequirementProvider add(String identifier, Class<TTarget> targetClass, Requirement<TTarget> requirement);

    <TRequirement extends Requirement<TTarget>, TTarget> RequirementProvider add(Class<TRequirement> requirementClass, ArtObjectProvider<TRequirement> requirement);
}
