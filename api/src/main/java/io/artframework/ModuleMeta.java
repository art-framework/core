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
import io.artframework.impl.DefaultModuleMeta;

/**
 * The module meta provides meta information about a loaded module.
 * <p>
 * Every {@link ArtModule} creates meta data on initialization from the annotations attached to the module.
 */
public interface ModuleMeta {

    /**
     * Creates new module meta by extracting the information from the given annotations.
     *
     * @param moduleClass the class of the module meta data should be extracted from
     * @return a new default module meta instance
     */
    static ModuleMeta of(Class<?> moduleClass) throws ArtMetaDataException {

        if (!moduleClass.isAnnotationPresent(ArtModule.class)) {
            throw new ArtMetaDataException(ArtObjectError.of(
                    moduleClass.getCanonicalName() + " is missing the required @ArtModule annotation.",
                    ArtObjectError.Reason.NO_ANNOTATION,
                    moduleClass)
            );
        }

        return new DefaultModuleMeta(moduleClass, moduleClass.getAnnotation(ArtModule.class));
    }

    /**
     * @return the unique identifier of the module
     */
    String identifier();

    /**
     * @return the class of the module.
     */
    Class<?> moduleClass();

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

    /**
     * @return a list of packages that should be loaded by the art-framework
     */
    String[] packages();

    /**
     * @return the prefix that should be used on all art objects
     */
    String prefix();

    /**
     * @return true if the module is a bootstrap module
     */
    boolean bootstrapModule();
}
