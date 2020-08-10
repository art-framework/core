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

import io.artframework.impl.DefaultArtModuleProvider;
import io.artframework.impl.DefaultConfiguration;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * The module provider handles the registration and creation of all art modules.
 * <p>
 * Use it to add your {@link ArtModule} or load modules as JAR files from a given path.
 */
public interface ArtModuleProvider extends Provider {

    /**
     * Creates a new default instance of the module provider.
     *
     * @param configuration the configuration instance to use
     * @return a new default instance of the module provider
     */
    static ArtModuleProvider of(DefaultConfiguration configuration) {
        return new DefaultArtModuleProvider(configuration);
    }

    /**
     * Registers the given resolve with the module provider.
     * <p>
     * The provider will use the given resolver when a module has unknown dependencies.
     * Set the resolver to null to clear out the default resolver and only use registered modules.
     *
     * @param resolver the resolver that should be used when
     * @return this module provider
     */
    ArtModuleProvider resolver(@Nullable ArtModuleDependencyResolver resolver);

    /**
     * @return the currently registered resolver
     */
    Optional<ArtModuleDependencyResolver> resolver();

    /**
     * Registers the given module with the provider, but does not enable it.
     * <p>
     * This is useful if you need to load multiple modules that depend upon each other
     * and do not want to directly enable them.
     * <p>
     * If you want to directly load and enable your module, call {@link #load(ArtModule)} instead.
     *
     * @param module the module that should be registered
     * @return this module provider
     * @throws ModuleRegistrationException if the registration of the module failed
     */
    ArtModuleProvider register(@NonNull ArtModule module) throws ModuleRegistrationException;

    /**
     * Loads the given module into the art-framework configuration instance.
     * <p>
     * This will try to load the configuration of the module (if one is needed),
     * check the dependencies and then call the {@link ArtModule#onEnable(Configuration)} method.
     *
     * @param module the module that should be loaded
     * @return this module provider
     * @throws ModuleRegistrationException if the registration or enabling of the module or its child modules failed
     */
    ArtModuleProvider load(@NonNull ArtModule module) throws ModuleRegistrationException;

    /**
     * Unloads the given module from the art-framework configuration instance.
     * <p>
     * This will call {@link ArtModule#onDisable(Configuration)} on the provided module
     * after all modules that depend on it have been disabled.
     *
     * @param module the module that should be unloaded
     * @return this module provider
     */
    ArtModuleProvider unload(@NonNull ArtModule module);
}
