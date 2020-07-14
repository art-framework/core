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

import net.silthus.art.conf.Settings;

import java.io.File;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * The {@link ArtProvider} holds and handles all ART that is registered.
 * Use it to register your ART or to retrieve registered implementations for a given identifier.
 * <br>
 * <p>You can also scan a path or JAR file and automatically register all ART that is found.
 * Use the {@link #registerAll(File)} method for that, or {@link #registerAll()} if you want to scan
 * the whole class path and add everything that was found.</p>
 * <p>By default the ART-Framework will scan the whole classpath. You can disable this by
 * setting {@link Settings#setAutoRegisterAllArt(boolean)}</p> to false.
 */
public interface ArtProvider {

    Configuration configuration();

    ArtFinder find();

    ArtProvider registerAll();

    ArtProvider registerAll(File file);

    ArtProvider registerAll(File file, Predicate<File> filePredicate);

    ArtProvider registerAll(Collection<ArtObjectInformation> artObjects);

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
