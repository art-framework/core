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
import io.artframework.events.EventListener;
import io.artframework.impl.DefaultConfiguration;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Collection;
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
     * Creates a new ArtBuilder from this configuration.
     * <p>
     * Use the {@link ArtBuilder} to load and create your ART from config files
     * with a parser.
     *
     * @return new art builder from this configuration.
     */
    default ArtBuilder builder() {
        return ArtBuilder.of(this);
    }

    /**
     * Parses the given art config into an art context.
     * <p>
     * This is just a shortcut to the {@link ArtBuilder#load(Collection)} method.
     *
     * @param lines the lines that should be parsed
     * @return the created art context
     * @see ArtBuilder#load(Collection)
     */
    default ArtContext load(Collection<String> lines) {
        return builder().load(lines).build();
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
     * with the {@link FactoryProvider#get(String)} method.
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
     * Use the {@link EventProvider} to register your {@link EventListener}s.
     *
     * @return the configured {@link EventProvider} implementation
     */
    EventProvider events();

    /**
     * Gets the configured {@link Scheduler} implementation.
     * You can provide your own by calling {@link #scheduler(Scheduler)}
     * which will replace the service you get from this method
     * with your own implementation.
     *
     * @return the configured {@link Scheduler} implementation
     */
    Optional<Scheduler> scheduler();

    /**
     * Gets the configured {@link Storage} implementation.
     * You can provide your own by calling {@link #storage(Storage)}
     * which will replace the service you get from this method
     * with your own implementation.
     *
     * @return the configured {@link Storage} implementation
     */
    Storage storage();

    /**
     * Gets the configured {@link Settings} of this configuration.
     * You can provide your own settings by calling {@link #settings(Settings)}.
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
    ArtSettings artSettings();

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
     * Use the {@link ArtModuleProvider} to load your art modules.
     *
     * @return the implementing {@link ArtModuleProvider}
     */
    ArtModuleProvider modules();

    /**
     * Use the {@link FinderProvider} to register your {@link AbstractFinder}s.
     *
     * @return the implementing {@link FinderProvider}
     */
    FinderProvider finder();

    /**
     * Gets the class loader that should be used to load additional classes.
     * By default the class loader of the configuration class will be used.
     * <p>
     * Set your custom class loader with the {@link #classLoader(ClassLoader)} method.
     *
     * @return the provided class loader
     */
    ClassLoader classLoader();

    /**
     * Sets a new implementation for the {@link ArtProvider}.
     *
     * @param artProvider art provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration art(@NonNull ArtProvider artProvider);

    /**
     * Sets a new implementation for the {@link Scheduler}.
     *
     * @param scheduler scheduler implementation to use
     * @return this {@link Configuration}
     */
    Configuration scheduler(Scheduler scheduler);

    /**
     * Sets a new implementation for the {@link Storage}.
     *
     * @param storage storage implementation to use
     * @return this {@link Configuration}
     */
    Configuration storage(@NonNull Storage storage);

    /**
     * Provides a new set of settings to use in this context.
     *
     * @param settings settings to use
     * @return this {@link Configuration}
     */
    Configuration settings(@NonNull Settings settings);

    /**
     * Provides new {@link ArtSettings} that will be used
     * in the creation of all {@link ArtContext} objects.
     *
     * @param settings new default {@link ArtSettings}
     * @return this {@link Configuration}
     */
    Configuration artSettings(@NonNull ArtSettings settings);

    /**
     * Sets a new implementation for the {@link TargetProvider}.
     *
     * @param targetProvider target provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration targets(@NonNull TargetProvider targetProvider);

    /**
     * Sets a new implementation for the {@link ActionProvider}.
     *
     * @param actionProvider {@link ActionProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration actions(@NonNull ActionProvider actionProvider);

    /**
     * Sets a new implementation for the {@link RequirementProvider}.
     *
     * @param requirementProvider {@link RequirementProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration requirements(@NonNull RequirementProvider requirementProvider);

    /**
     * Sets a new implementation for the {@link TriggerProvider}.
     *
     * @param triggerProvider trigger provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration trigger(@NonNull TriggerProvider triggerProvider);

    /**
     * Sets a new implementation for the {@link EventProvider}.
     *
     * @param eventProvider {@link EventProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration events(@NonNull EventProvider eventProvider);

    /**
     * Sets a new implementation for the {@link FlowParserProvider}.
     *
     * @param flowParserProvider {@link FlowParserProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration parser(@NonNull FlowParserProvider flowParserProvider);

    /**
     * Sets a new implementation for the {@link ArtModuleProvider}.
     *
     * @param moduleProvider {@link ArtModuleProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration modules(@NonNull ArtModuleProvider moduleProvider);

    /**
     * Sets a new implementation for the {@link FinderProvider}.
     *
     * @param finderProvider {@link FinderProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration finder(@NonNull FinderProvider finderProvider);

    /**
     * Provides an alternate class loader that should be used when loading additional classes.
     * For example in a {@link AbstractFinder}.
     *
     * @param classLoader the class loader to use
     * @return this {@link Configuration}
     */
    Configuration classLoader(@NonNull ClassLoader classLoader);

    /**
     * Creates a new {@link Configuration} derived from this configuration.
     * This is actually just a shortcut to {@link #clone()}.
     * You can then use the configuration to modify locally scoped properties
     * and use it to load and create your ART with {@link ART#builder(Configuration)}.
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
    Configuration withArt(@NonNull ArtProvider artProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link Scheduler}.
     *
     * @param scheduler scheduler implementation to use
     * @return this {@link Configuration}
     */
    Configuration withScheduler(Scheduler scheduler);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link Storage}.
     *
     * @param storage storage implementation to use
     * @return this {@link Configuration}
     */
    Configuration withStorage(@NonNull Storage storage);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new set of settings to use in this context.
     *
     * @param settings settings to use
     * @return this {@link Configuration}
     */
    Configuration withSettings(@NonNull Settings settings);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and provides new {@link ArtSettings} that will be used
     * in the creation of all {@link ArtContext} objects.
     *
     * @param settings new default {@link ArtSettings}
     * @return this {@link Configuration}
     */
    Configuration withArtSettings(@NonNull ArtSettings settings);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link TargetProvider}.
     *
     * @param targetProvider target provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration withTargets(@NonNull TargetProvider targetProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link ActionProvider}.
     *
     * @param actionProvider {@link ActionProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration withActions(@NonNull ActionProvider actionProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link RequirementProvider}.
     *
     * @param requirementProvider {@link RequirementProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration withRequirements(@NonNull RequirementProvider requirementProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link TriggerProvider}.
     *
     * @param triggerProvider trigger provider implementation to use
     * @return this {@link Configuration}
     */
    Configuration withTrigger(@NonNull TriggerProvider triggerProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link EventProvider}.
     *
     * @param eventProvider {@link EventProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration withEvents(@NonNull EventProvider eventProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link FlowParserProvider}.
     *
     * @param flowParserProvider {@link FlowParserProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration withParser(@NonNull FlowParserProvider flowParserProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link ArtModuleProvider}.
     *
     * @param moduleProvider {@link ArtModuleProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration withModules(@NonNull ArtModuleProvider moduleProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link FinderProvider}.
     *
     * @param finderProvider {@link FinderProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration withFinder(@NonNull FinderProvider finderProvider);

    /**
     * Creates a new {@link Configuration} derived from this configuration
     * and sets a new implementation for the {@link ClassLoader}.
     *
     * @param classLoader {@link FinderProvider} implementation to use
     * @return this {@link Configuration}
     */
    Configuration withClassLoader(@NonNull ClassLoader classLoader);
}
