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

/**
 * Defines the state of a module provided by the module provider.
 * <p>
 * The art module can have multiple states during its lifecycle,
 * defining the different stages of module initialization and loading of dependencies.
 */
public enum ModuleState {

    /**
     * The module is in an unknown state.
     */
    UNKNOWN,
    /**
     * The module has been registered but is not loaded or enabled.
     */
    REGISTERED,
    /**
     * The module has been registered and bootstrapping is complete.
     */
    BOOTSTRAPPED,
    /**
     * The module has been loaded.
     */
    LOADED,
    /**
     * The module is successfully enabled and running.
     */
    ENABLED,
    /**
     * The module is disabled.
     */
    DISABLED,
    /**
     * The enabling or loading of the module has been delayed.
     */
    DELAYED,
    /**
     * The module encountered an error during load or initialization.
     */
    ERROR,
    /**
     * The module cannot be enabled because it has missing dependencies.
     */
    MISSING_DEPENDENCIES,
    /**
     * The module cannot be enabled because it has cyclic dependencies.
     */
    CYCLIC_DEPENDENCIES,
    /**
     * One of the dependencies could not be enabled.
     */
    DEPENDENCY_ERROR,
    /**
     * A duplicate module with the same identifier but different class exists.
     */
    DUPLICATE_MODULE,
    /**
     * The module is invalid and does not contain a valid annotation.
     */
    INVALID_MODULE;

    public boolean error() {
        return this == INVALID_MODULE
                || this == DUPLICATE_MODULE
                || this == DEPENDENCY_ERROR
                || this == CYCLIC_DEPENDENCIES
                || this == MISSING_DEPENDENCIES
                || this == ERROR
                || this == UNKNOWN;
    }

    public boolean bootstrapped() {
        return this == BOOTSTRAPPED;
    }

    public boolean loaded() {
        return this == LOADED;
    }

    public boolean enabled() {
        return this == ENABLED;
    }

    public boolean disabled() {
        return this == DISABLED;
    }

    public boolean canBootstrap() {

        return !bootstrapped() && !loaded() && !enabled() && !disabled() && !error() ;
    }

    public boolean canLoad() {
        return !loaded() && !enabled() && !disabled() && !error();
    }

    public boolean canReload() {
        return enabled();
    }

    public boolean canEnable() {
        return !enabled() && !disabled() && !error();
    }

    public boolean canDisable() {
        return !disabled() && !error();
    }
}
