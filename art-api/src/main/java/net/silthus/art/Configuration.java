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

package net.silthus.art;

/**
 * Use the Configuration to retrieve and replace all elements of the ART-Framework.
 * You can for example provide your own {@link Scheduler} or {@link Storage} implementations.
 */
public interface Configuration {

    /**
     * Gets the configured {@link Scheduler} implementation.
     * You can provide your own by calling {@link #set(Scheduler)}
     * which will replace the service you get from this method
     * with your own implementation.
     *
     * @return the configured {@link Scheduler} implementation
     */
    Scheduler scheduler();

    /**
     * Gets the configured {@link Storage} implementation.
     * You can provide your own by calling {@link #set(Storage)}
     * which will replace the service you get from this method
     * with your own implementation.
     *
     * @return the configured {@link Storage} implementation
     */
    Storage storage();

    /**
     * Adds a {@link TargetProvider} for the given {@link Target} type.
     * Will override any existing {@link TargetProvider} of the same target type.
     *
     * @param targetClass class of the target you want to add
     * @param targetProvider {@link TargetProvider} that creates the {@link Target} for the given type
     * @param <TTarget> type of the target
     * @return this {@link Configuration}
     */
    <TTarget> Configuration set(Class<TTarget> targetClass, TargetProvider<TTarget> targetProvider);

    /**
     * Adds a {@link ArtObjectProvider} for the given {@link ArtObject} type.
     * The provider will be used to create instances of the given {@link ArtObject}.
     * Will override any existing {@link ArtObjectProvider} fo the same type.
     *
     * @param artObjectClass class of the {@link ArtObject}
     * @param artObjectProvider {@link ArtObjectProvider} that should be added
     * @param <TArtObject> type of the {@link ArtObject}
     * @return this {@link Configuration}
     */
    <TArtObject extends ArtObject> Configuration set(Class<TArtObject> artObjectClass, ArtObjectProvider<TArtObject> artObjectProvider);

    /**
     * Sets a new implementation for the {@link Scheduler}.
     *
     * @param scheduler scheduler implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(Scheduler scheduler);

    /**
     * Sets a new implementation for the {@link Storage}.
     *
     * @param storage storage implementation to use
     * @return this @{@link Configuration}
     */
    Configuration set(Storage storage);

    /**
     * Removes all existing {@link TargetProvider} from this {@link Configuration}.
     * <br>
     * Make sure you add your {@link TargetProvider} implementations afterwards or
     * everything will silently fails since there will be no {@link Target} type wrappers.
     *
     * @return this {@link Configuration}
     */
    Configuration removeAllTargetProvider();
}
