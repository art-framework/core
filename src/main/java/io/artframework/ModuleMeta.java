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

import io.artframework.annotations.ART;
import io.artframework.annotations.Depends;
import io.artframework.impl.DefaultModuleMeta;
import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * The module meta provides meta information about a loaded module.
 * <p>
 * Every {@link Module} creates meta data on initialization from the annotations attached to the module.
 */
public interface ModuleMeta {

    /**
     * Creates new module meta by extracting the information from the given annotations.
     *
     * @param art the art annotation of the module class
     * @param depends the optional dependency annotation of the module class. can be null.
     * @return a new default module meta instance
     */
    static ModuleMeta of(Class<?> moduleClass, @NonNull ART art, @Nullable Depends depends) {

        return new DefaultModuleMeta(moduleClass, art, depends);
    }

    /**
     * @return the unique identifier of the module
     */
    String identifier();

    Class<?> moduleClass();

    /**
     * @return a list of aliases of the module
     */
    String[] alias();

    /**
     * @return the description of the module
     */
    String[] description();

    /**
     * @return the version of the module
     */
    String version();

    /**
     * @return a list of module identifiers this module depends on
     */
    String[] dependencies();
}
