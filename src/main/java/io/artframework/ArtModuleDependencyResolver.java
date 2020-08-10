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

import java.util.concurrent.CompletableFuture;

/**
 * The module dependency resolver tries to resolve all dependencies and
 * indicates if the module can be loaded or not.
 * <p>
 * Make sure to register the dependency resolver with the {@link ArtModuleProvider}.
 * <p>
 * <h4>Implementation notice</h4>
 * Make sure you register all resolved modules before you return the future result.
 * Use the {@link ArtModuleProvider#register(ArtModule)} method for that.
 */
public interface ArtModuleDependencyResolver {

    /**
     * Tries to resolve all dependencies of the given module in any way possible
     * and then registers the resolved modules with the module provider.
     * <p>
     * May use an asynchronous thread and return the future after the resolution is complete.
     *
     * @param moduleMeta the metadata of the module that should be resolved
     * @return the future result of the module resolution
     */
    CompletableFuture<ModuleResolution> resolve(ModuleMeta moduleMeta);
}
