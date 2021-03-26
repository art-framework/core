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

import io.artframework.annotations.ArtModule;
import io.artframework.impl.DefaultModuleProvider;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * The module provider handles the registration and creation of all art modules.
 * <p>
 * Use it to add your modules that are annotated with the @{@link ArtModule} annotation.
 */
public interface ModuleProvider extends Provider {

    /**
     * Creates a new default instance of the module provider.
     *
     * @param scope the configuration instance to use
     * @return a new default instance of the module provider
     */
    static ModuleProvider getSourceModule(Scope scope) {
        return new DefaultModuleProvider(scope);
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
    ModuleProvider resolver(@Nullable ArtModuleDependencyResolver resolver);

    /**
     * @return the currently registered resolver
     */
    Optional<ArtModuleDependencyResolver> resolver();

    /**
     * Bootstraps the given module and all of the supplied sub modules.
     *
     * @param bootstrapScope the bootstrap scope containing the initial module
     * @return the bootstrap phase which is used to load and enable all modules after configuration is finished and sealed
     */
    ModuleProvider bootstrap(BootstrapScope bootstrapScope) throws BootstrapException;

    /**
     * Loads all modules in the scope.
     */
    void loadAll();

    /**
     * Enables all modules in the scope.
     */
    void enableAll();

    /**
     * Disables all modules in the scope.
     */
    void disableAll();

    /**
     * Registers the given module with the provider and then loads
     * and enables it based on the current lifecycle state of the art-framework.
     * <p>
     * Make sure the class is annotated with @{@link ArtModule} or the registration will fail with an exception.
     *
     * @param module the module that should be registered
     * @return this module provider
     * @throws ModuleRegistrationException if the registration of the module failed,
     *                                     e.g. if no {@code @ArtModule} annotation is present on the class
     */
    ModuleProvider register(@NonNull Module module) throws ModuleRegistrationException;

    /**
     * Registers the given class as a module with the provider and then loads
     * and enables it based on the current lifecycle state of the art-framework.
     * <p>
     * The class must provide a parameterless public constructor or the module won't be registered.
     * Also make sure it is annotated with @{@link ArtModule} or else the registration will fail also.
     * <p>
     * Use {@link #register(Module)} if you already have an instance of your module, e.g. it was loaded as by a plugin.
     *
     * @param moduleClass the class of the module
     * @return this module provider
     * @throws ModuleRegistrationException if the registration or instance creation of the module failed,
     *                                     e.g. if no {@code @ArtModule} annotation is present on the class
     *                                     or no instance of the module could be created.
     */
    ModuleProvider register(@NonNull Class<? extends Module> moduleClass) throws ModuleRegistrationException;

    /**
     * Enables the given module instance registered with the art-framework.
     * <p>
     * The module must be registered ({@link #register(Module)} or {@link #register(Class)}) before using this method.
     * The method primarily exists to enable disabled modules on the fly,
     * because any module that is registered will be enabled anyways.
     *
     * @param moduleClass the class of the module
     * @return this module provider
     */
    ModuleProvider enable(@NonNull Class<? extends Module> moduleClass);

    /**
     * Disabled the given module and unloads it from the art-framework scope.
     *
     * @param module the module that should be unloaded
     * @return this module provider
     */
    ModuleProvider disable(@NonNull Class<? extends Module> module);

    /**
     * Reloads the module with the given class if it exists and is enabled.
     *
     * @param moduleClass the class of the module that should be reloaded
     * @return this module provider
     */
    ModuleProvider reload(@NonNull Class<? extends Module> moduleClass);

    /**
     * Reloads all enabled modules calling the tagged reload method if it is present.
     *
     * @return this module provider
     */
    ModuleProvider reloadAll();

    /**
     * Tries to find a registered module for the given module class.
     * <p>The module must be registered with the provider, but may be
     * disabled and not active.
     *
     * @param moduleClass the module class to get the registered instance for
     * @return the registered module instance or an empty optional
     */
    <TModule extends Module> Optional<TModule> get(@NonNull Class<TModule> moduleClass);

    /**
     * Tries to find the metadata information for the given module class.
     *
     * @param moduleClass the module class to find the metadata information for
     * @return the metadata for the given module if the module is registered
     */
    Optional<ModuleMeta> getMetadata(@NonNull Class<? extends Module> moduleClass);

    /**
     * Tries to find the module that contains the given class.
     * <p>This will compare the code source of the class with the code source of the modules.
     * If the given class is in the same code source as the module it is a match.
     * <p>Only the first module is returned if multiple modules exist inside the code source.
     *
     * @param clazz the class whos module should be found
     * @return the module of the class
     */
    Optional<ModuleMeta> getSourceModule(@NonNull Class<?> clazz);

    /**
     * Gets all registered modules regardless of their current state.
     *
     * @return all registered modules
     */
    Collection<ModuleMeta> all();
}
