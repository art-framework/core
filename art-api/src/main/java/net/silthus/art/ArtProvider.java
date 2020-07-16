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

import java.util.Collection;

/**
 * The {@link ArtProvider} holds and handles all ART that is registered.
 * Use it to register your ART or to retrieve registered implementations for a given identifier.
 * <br>
 * You can also scan a path or JAR file and automatically register all ART that is found.
 * Use the {@link #find()} and the {@link ArtFinder} to find specific ART in your files
 * and register the {@link ArtFinderResult} with the {@link ArtFinderResult#register()} method.
 * <br>
 * By default the ART-Framework will scan the whole classpath. You can disable this by
 * setting {@link Settings#setAutoRegisterAllArt(boolean)} to <pre>false</pre>.
 */
public interface ArtProvider extends Provider {

    /**
     * Gets the {@link ArtFinder} to find {@link ArtObject}s inside your
     * classpath or file tree. Use the {@link ArtFinderResult} to register
     * the found art objects with the {@link ArtProvider}.
     *
     * @return The {@link ArtFinder} to find {@link ArtObject}s in your filesystem and classpath.
     */
    default ArtFinder find() {
        return configuration().findArt();
    }

    /**
     * Adds all of the provided {@link ArtObject}s to the relevant {@link ArtProvider}.
     * Make sure that {@link ArtObjectInformation#isInitialized()} returns true.
     * Otherwise the registration will fail.
     *
     * @param artObjects The list of {@link ArtObject}s that should be added to the provider
     * @return this {@link ArtProvider}
     */
    ArtProvider addAll(Collection<ArtObjectInformation<?>> artObjects);

    default ActionProvider actions() {
        return configuration().actions();
    }

    default RequirementProvider requirements() {
        return configuration().requirements();
    }

    default TriggerProvider trigger() {
        return configuration().trigger();
    }
}
