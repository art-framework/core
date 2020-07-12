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

import java.io.File;

public interface ArtProvider {

    Configuration configuration();

    ArtProvider findAll(File file);

    ArtProvider action(Class<? extends Action<?>> actionClass);

    <TTarget> ArtProvider action(Action<TTarget> action);

    <TAction extends Action<?>> ArtProvider action(Class<TAction> actionClass, ArtObjectProvider<TAction> actionProvider);

    ArtProvider requirement(Class<? extends Requirement<?>> requirementClass);

    <TTarget> ArtProvider requirement(Requirement<TTarget> requirement);

    <TRequirement extends Requirement<?>> ArtProvider requirement(Class<TRequirement> requirementClass, ArtObjectProvider<TRequirement> requirementProvider);

    ArtProvider trigger(Class<? extends Trigger> triggerClass);

    ArtProvider trigger(Trigger trigger);

    <TTrigger extends Trigger> ArtProvider trigger(Class<TTrigger> triggerClass, ArtObjectProvider<TTrigger> triggerProvider);
}
