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

import io.artframework.conf.ArtSettings;
import io.artframework.conf.Settings;
import io.artframework.events.ArtEventListener;
import io.artframework.impl.DefaultConfiguration;
import lombok.NonNull;

import java.io.File;
import java.io.Serializable;
import java.util.Optional;

/**
 * Use the Configuration to retrieve and replace all elements of the ART-Framework.
 * You can for example provide your own {@link Scheduler} or {@link Storage} implementations.
 */
public interface Configuration extends Serializable, Cloneable {

    static Configuration create() {
        return new DefaultConfiguration();
    }

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
     * Gets the {@link ArtSettings} that will be used by default
     * when creating a new {@link ArtContext}.
     *
     * @return default {@link ArtSettings} used in an {@link ArtContext}
     */
    ArtSettings contextSettings();

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
     * Provides new {@link ArtSettings} that will be used
     * in the creation of all {@link ArtContext} objects.
     *
     * @param settings new default {@link ArtSettings}
     * @return this {@link Configuration}
     */
    Configuration set(@NonNull ArtSettings settings);

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
     * Creates a new {@link Configuration} derived from this configuration.
     * This is actually just a shortcut to {@link #clone()}.
     * You can then use the configuration to modify locally scoped properties
     * and use it to load and create your ART with {@link ArtContext#builder(Configuration)}.
     *
     * @return this cloned {@link Configuration}
     */
    Configuration derive();

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link ArtProvider}.
     *
     * @param artProvider art provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull ArtProvider artProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link Scheduler}.
     *
     * @param scheduler scheduler implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(Scheduler scheduler);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link Storage}.
     *
     * @param storage storage implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull Storage storage);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new set of settings to use in this context.
     *
     * @param settings settings to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull Settings settings);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and provides new {@link ArtSettings} that will be used
     * in the creation of all {@link ArtContext} objects.
     *
     * @param settings new default {@link ArtSettings}
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull ArtSettings settings);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link TargetProvider}.
     *
     * @param targetProvider target provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull TargetProvider targetProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link ActionProvider}.
     *
     * @param actionProvider {@link ActionProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull ActionProvider actionProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link RequirementProvider}.
     *
     * @param requirementProvider {@link RequirementProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull RequirementProvider requirementProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link TriggerProvider}.
     *
     * @param triggerProvider trigger provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull TriggerProvider triggerProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link ArtFinder}.
     *
     * @param artFinder {@link ArtFinder} implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull ArtFinder artFinder);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link EventProvider}.
     *
     * @param eventProvider {@link EventProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull EventProvider eventProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link FlowParserProvider}.
     *
     * @param flowParserProvider {@link FlowParserProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration derive(@NonNull FlowParserProvider flowParserProvider);
}
