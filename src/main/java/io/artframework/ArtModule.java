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

import io.artframework.annotations.Config;
import io.artframework.annotations.Module;

/**
 * An art-framework module provides one or multiple functions and/or art objects.
 * <p>
 * Every module must also be annotated with the @{@link Module} annotation and provide an unique identifier.
 * It is also considered best practice to provide a good description about what your module does.
 * <p>
 * Every functionality or additional art objects are part of a module that will be loaded when the art-framework starts.
 * Use the {@link #onEnable(Configuration)} method to register your art or service provider with the configuration instance.
 * <p>
 * Make sure you cleanup any cache and unregister all listeners when {@link #onDisable(Configuration)} is called.
 * The art-framework may call the enable and disable methods multiple times in a lifecycle, e.g. when reloading.
 * <p>
 * Implement the {@link Configurable} interface to load configurations for your module from a config file.
 * The load method will be called before the first onEnable call and everytime the config is reloaded.
 * By default the identifier of your module will be used as the config file name.
 * You can define an alternative file name with the @{@link Config} annotation.
 */
public interface ArtModule {

    /**
     * This method is called after all modules have been loaded and when this module is enabled.
     * <p>
     * This means that any dependencies defined in your @{@link Module#dependencies()} reference will be resolved
     * or else this module won't be loaded.
     *
     * @param configuration the configuration instance that loaded this module
     */
    default void onEnable(Configuration configuration) {}

    /**
     * This method is called when your module is disabled.
     * <p>
     * Make sure you cleanup any stale resources and caches, since onEnable might be called shortly
     * afterwards if your module is just reloading.
     *
     * @param configuration the configuration instance that loaded this module
     */
    default void onDisable(Configuration configuration) {}
}
