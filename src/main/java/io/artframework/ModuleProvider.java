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
     * @param configuration the configuration instance to use
     * @return a new default instance of the module provider
     */
    static ModuleProvider of(Configuration configuration) {
        return new DefaultModuleProvider(configuration);
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
     * Registers the given module with the provider, but does not enable it.
     * <p>
     * This is useful if you need to load multiple modules that depend upon each other
     * and do not want to directly enable them.
     * <p>
     * Make sure the class is annotated with @{@link ArtModule} or the registration will fail with an exception.
     * <p>
     * If you want to directly load and enable your module, call {@link #enable(Object)} instead.
     * And if you don't have an instance of your module use the {@link #register(Class)} method instead.
     *
     * @param module the module that should be registered
     * @return this module provider
     * @throws ModuleRegistrationException if the registration of the module failed,
     *                                     e.g. if no {@code @ArtModule} annotation is present on the class
     */
    ModuleProvider register(@NonNull Object module) throws ModuleRegistrationException;

    /**
     * Registers the given class as a module with the provider.
     * <p>
     * Use this if you do not have an instance of the module and one should be created for you.
     * This could be the case if the module was loaded from a classpath search inside one of the module JAR files.
     * <p>
     * The class must provide a parameterless public constructor or the module won't be registered.
     * Also make sure it is annotated with @{@link ArtModule} or else the registration will fail also.
     * <p>
     * Use {@link #register(Object)} if you already have an instance of your module, e.g. it was loaded as a plugin.
     *
     * @param moduleClass the class of the module
     * @return this module provider
     * @throws ModuleRegistrationException if the registration or instance creation of the module failed,
     *                                     e.g. if no {@code @ArtModule} annotation is present on the class
     *                                     or no instance of the module could be created.
     */
    ModuleProvider register(@NonNull Class<?> moduleClass) throws ModuleRegistrationException;

    /**
     * Loads the given module into the art-framework configuration instance and enables it.
     * <p>
     * This will call any methods inside the module that are annotated with one of the {@code On...} annotations.
     * <p>
     * Make sure the class is annotated with @{@link ArtModule} or the registration will fail with an exception.
     *
     * @param module the module that should be loaded
     * @return this module provider
     * @throws ModuleRegistrationException if the registration of the module failed,
     *                                     e.g. if no {@code @ArtModule} annotation is present on the class
     *                                     or if one of the annotated methods encountered an exception.
     * @see io.artframework.annotations.OnEnable
     * @see io.artframework.annotations.OnDisable
     * @see io.artframework.annotations.OnLoad
     */
    ModuleProvider enable(@NonNull Object module) throws ModuleRegistrationException;

    /**
     * Loads the given module into the art-framework configuration instance and enables it.
     * <p>
     * Use this if you do not have an instance of the module and one should be created for you.
     * This could be the case if the module was loaded from a classpath search inside one of the module JAR files.
     * <p>
     * The class must provide a parameterless public constructor or the module won't be registered.
     * Also make sure it is annotated with @{@link ArtModule} or else the registration will fail also.
     * <p>
     * This will then call any methods inside the module that are annotated with one of the {@code On...} annotations.
     * <p>
     * Use {@link #enable(Object)} if you already have an instance of your module, e.g. it was loaded as a plugin.
     *
     * @param moduleClass the class of the module
     * @return this module provider
     * @throws ModuleRegistrationException if the registration or instance creation of the module failed,
     *                                     e.g. if no {@code @ArtModule} annotation is present on the class,
     *                                     no instance of the module could be created
     *                                     or if one of the annotated methods encountered an exception.
     * @see io.artframework.annotations.OnEnable
     * @see io.artframework.annotations.OnDisable
     * @see io.artframework.annotations.OnLoad
     */
    ModuleProvider enable(@NonNull Class<?> moduleClass) throws ModuleRegistrationException;

    /**
     * Disabled the given module and unloads it from the art-framework configuration instance.
     *
     * @param module the module that should be unloaded
     * @return this module provider
     */
    ModuleProvider disable(@NonNull Object module);
}
