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

import java.util.Collection;

public interface BootstrapModule {

    /**
     * Gets a list of submodules that should be bootstrapped together with this module.
     * <p>
     * The list may contained mixed objects and classes, but all must be annotated with the {@link ArtModule} annotation.
     * An empty list must be returned if no modules exist.
     *
     * @return a list of submodules in this module
     */
    Collection<Object> modules();

    void enable(BootstrapScope scope);

    void disable(Scope scope);
}
