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

import lombok.NonNull;
import net.silthus.art.conf.ArtContextSettings;
import net.silthus.art.conf.Settings;
import net.silthus.art.events.ArtEventListener;
import net.silthus.art.impl.DefaultConfiguration;

import java.io.File;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

/**
 * Use the Configuration to retrieve and replace all elements of the ART-Framework.
 * You can for example provide your own {@link Scheduler} or {@link Storage} implementations.
 */
public interface Configuration extends Serializable, Cloneable {

    Configuration DEFAULT = new DefaultConfiguration();

    /**
     * Use the {@link ArtProvider} and its sub provider to register your ART.
     * Sub providers are:
     * <ul>
     *     <li>{@link ActionProvider}</li>
     *     <li>{@link RequirementProvider}</li>
     *     <li>{@link TriggerProvider}</li>
     * </ul>
     *
     * @return the configured {@link ArtProvider} implementation
     */
    ArtProvider art();

    /**
     * Use the {@link ActionProvider} to register and query your {@link Action}s.
     * Register actions with the add(...) methods and get an existing {@link ActionFactory}
     * with the {@link ActionProvider#get(String)} method.
     *
     * @return the configured {@link ActionProvider} implementation
     */
    ActionProvider actions();

    /**
     * Use the {@link RequirementProvider} to register and query your {@link Requirement}s.
     * Register requirements with the add(...) methods and get an existing {@link RequirementFactory}
     * with the {@link ArtFactoryProvider#get(String)} method.
     *
     * @return the configured {@link ActionProvider} implementation
     */
    RequirementProvider requirements();

    /**
     * Use the {@link ActionProvider} to register and query your {@link Action}s.
     * Register actions with the add(...) methods and get an existing {@link ActionFactory}
     * with the {@link ActionProvider#get(String)} method.
     *
     * @return the configured {@link ActionProvider} implementation
     */
    TriggerProvider trigger();

    /**
     * Use the {@link ArtFinder} to find all of your {@link ArtObject}s inside
     * {@link File}s and the classpath. You can then register the found ART.
     *
     * @return the configured {@link ArtFinder} implementation
     */
    ArtFinder findArt();

    /**
     * Use the {@link EventProvider} to register your {@link ArtEventListener}s.
     *
     * @return the configured {@link EventProvider} implementation
     */
    EventProvider events();

    /**
     * Gets the configured {@link Scheduler} implementation.
     * You can provide your own by calling {@link #set(Scheduler)}
     * which will replace the service you get from this method
     * with your own implementation.
     *
     * @return the configured {@link Scheduler} implementation
     */
    Optional<Scheduler> scheduler();

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
     * Gets the {@link ArtContextSettings} that will be used by default
     * when creating a new {@link ArtContext}.
     *
     * @return default {@link ArtContextSettings} used in an {@link ArtContext}
     */
    ArtContextSettings contextSettings();

    /**
     * Use the {@link TargetProvider} to register new {@link Target} source types
     * or get a {@link Target} for any given source.
     *
     * @return the implementing {@link TargetProvider}
     */
    TargetProvider targets();

    /**
     * Use the {@link FlowParserProvider} to register your custom {@link FlowParser}
     * that will be used when parsing flow configurations.
     *
     * @return the implementing {@link FlowParserProvider}
     */
    FlowParserProvider parser();

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
    Configuration set(@NonNull ArtProvider artProvider);

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
    Configuration set(@NonNull Storage storage);

    /**
     * Provides a new set of settings to use in this context.
     *
     * @param settings settings to use
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull Settings settings);

    /**
     * Provides new {@link ArtContextSettings} that will be used
     * in the creation of all {@link ArtContext} objects.
     *
     * @param settings new default {@link ArtContextSettings}
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull ArtContextSettings settings);

    /**
     * Sets a new implementation for the {@link TargetProvider}.
     *
     * @param targetProvider target provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull TargetProvider targetProvider);

    /**
     * Sets a new implementation for the {@link ActionProvider}.
     *
     * @param actionProvider {@link ActionProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull ActionProvider actionProvider);

    /**
     * Sets a new implementation for the {@link RequirementProvider}.
     *
     * @param requirementProvider {@link RequirementProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull RequirementProvider requirementProvider);

    /**
     * Sets a new implementation for the {@link TriggerProvider}.
     *
     * @param triggerProvider trigger provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull TriggerProvider triggerProvider);

    /**
     * Sets a new implementation for the {@link ArtFinder}.
     *
     * @param artFinder {@link ArtFinder} implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull ArtFinder artFinder);

    /**
     * Sets a new implementation for the {@link EventProvider}.
     *
     * @param eventProvider {@link EventProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull EventProvider eventProvider);

    /**
     * Sets a new implementation for the {@link FlowParserProvider}.
     *
     * @param flowParserProvider {@link FlowParserProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull FlowParserProvider flowParserProvider);

    /**
     * Creates a new {@link Configuration} derived from the current configuration.
     * This is actually just a shortcut to {@link #clone()}.
     * You can then use the configuration to modify locally scoped properties
     * and use it to load and create your ART with {@link ART#builder(Configuration)}.
     *
     * @return this cloned {@link Configuration}
     */
    Configuration derive();
}
