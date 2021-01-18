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
import lombok.NonNull;

/**
 * Marks the ArtObject or Module as configurable.
 * <p>
 * The art-framework will try to load the given config at runtime instantiating
 * a new object from the class. The config class must be public and have a parameterless constructor.
 * <p>
 * The {@link #load(Object)} method of this class will be called everytime the config is loaded or reloaded.
 * Make sure you implement your loading logic accordingly and reset any caches you might have.
 * <p>
 * You can also annotate your class, load method or config parameter in the load method with the @{@link Config} annotation.
 * This will allow it to load the config directly from a file based of the base path of the art-framework.
 * <p>
 * If you do not annotate it and the configurable class is a {@link io.artframework.annotations.ArtModule}, the identifier of the module will be used.
 *
 * @param <TConfig> the type of the config that should be loaded
 * @see Config
 */
public interface Configurable<TConfig> {

    /**
     * Loads the given config.
     * <p>
     * The method will also be called anytime the config is reloaded.
     *
     * @param config the config that is loaded
     */
    void load(@NonNull TConfig config);
}
