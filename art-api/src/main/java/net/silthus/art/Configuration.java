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

import net.silthus.art.conf.Settings;

import java.io.File;
import java.util.function.Function;

/**
 * Use the Configuration to retrieve and replace all elements of the ART-Framework.
 * You can for example provide your own {@link Scheduler} or {@link Storage} implementations.
 */
public interface Configuration {

    /**
     * Use the {@link ArtProvider} to add and register your {@link ArtObject}s.
     *
     * @return implementing {@link ArtProvider}
     */
    ArtProvider art();

    default Configuration findAllArt(File file) {
        art().findAll(file);
        return this;
    }

    default Configuration action(Class<? extends Action<?>> actionClass) {
        art().action(actionClass);
        return this;
    }

    default <TTarget> Configuration action(Action<TTarget> action) {
        art().action(action);
        return this;
    }

    default Configuration requirement(Class<? extends Requirement<?>> requirementClass) {
        art().requirement(requirementClass);
        return this;
    }

    default <TTarget> Configuration requirement(Requirement<TTarget> requirement) {
        art().requirement(requirement);
        return this;
    }

    default Configuration trigger(Class<? extends Trigger> triggerClass) {
        art().trigger(triggerClass);
        return this;
    }

    default Configuration trigger(Trigger trigger) {
        art().trigger(trigger);
        return this;
    }

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
     * Gets the configured {@link Settings} of this configuration.
     * You can provide your own settings by calling {@link #set(Settings)}.
     *
     * @return the {@link Settings} of this {@link Configuration}
     */
    Settings settings();

    /**
     * Use the {@link TargetProvider} to register new {@link Target} source types
     * or get a {@link Target} for any given source.
     *
     * @return the implementing {@link TargetProvider}
     */
    TargetProvider targets();

    /**
     * Adds a {@link TargetProvider} for the given {@link Target} type.
     * Will override any existing {@link TargetProvider} of the same target type.
     * <br>
     * This is just a convenience method and delegates to {@link TargetProvider#add(Class, Function)}.
     *
     * @param targetClass class of the target you want to add
     * @param targetProvider {@link TargetProvider} that creates the {@link Target} for the given type
     * @param <TTarget> type of the target
     * @return this {@link Configuration}
     * @see TargetProvider#add(Class, Function)
     */
    default <TTarget> Configuration target(Class<TTarget> targetClass, Function<TTarget, Target<TTarget>> targetProvider) {
        targets().add(targetClass, targetProvider);
        return this;
    }

    /**
     * Sets a new implementation for the {@link ArtProvider}.
     *
     * @param artProvider art provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(ArtProvider artProvider);

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
     * @return this {@link Configuration}
     */
    Configuration set(Storage storage);

    /**
     * Provides a new set of settings to use in this context.
     *
     * @param settings settings to use
     * @return this {@link Configuration}
     */
    Configuration set(Settings settings);

    /**
     * Sets a new implementation for the {@link TargetProvider}.
     *
     * @param targetProvider target provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(TargetProvider targetProvider);
}
