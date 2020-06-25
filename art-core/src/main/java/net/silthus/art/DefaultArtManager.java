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

import lombok.Data;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.parser.ArtParser;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFilter;
import net.silthus.art.api.requirements.RequirementFactory;
import net.silthus.art.api.requirements.RequirementManager;
import net.silthus.art.api.trigger.TriggerContext;
import net.silthus.art.util.ConfigUtil;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toMap;

@Data
public class DefaultArtManager implements ArtManager {

    private final ActionManager actionManager;
    private final RequirementManager requirementManager;

    private Logger logger = Logger.getLogger("ART");
    private final Map<String, Provider<ArtParser>> parser;
    private final Map<Class<?>, List<ArtResultFilter<?>>> globalFilters = new HashMap<>();
    private final Map<ArtModuleDescription, ArtBuilder> registeredPlugins = new HashMap<>();

    @Inject
    public DefaultArtManager(ActionManager actionManager, RequirementManager requirementManager, Map<String, Provider<ArtParser>> parser) {
        this.actionManager = actionManager;
        this.requirementManager = requirementManager;
        this.parser = parser;
    }

    ActionManager actions() {
        return actionManager;
    }

    RequirementManager requirements() {
        return requirementManager;
    }

    @Override
    public void load() {

        getLogger().info("-------- ART MANAGER LOADED --------");
    }

    @Override
    public void unload() {

        getLogger().info("-------- ART MANAGER UNLOADED --------");
    }

    @Override
    public void register(ArtModuleDescription moduleDescription, Consumer<ArtBuilder> builder) {

        ArtBuilder artBuilder;
        if (registeredPlugins.containsKey(moduleDescription)) {
            artBuilder = registeredPlugins.get(moduleDescription);
        } else {
            artBuilder = new ArtBuilder();
        }

        builder.andThen(art -> registeredPlugins.put(moduleDescription, art))
                .andThen(art -> {
                    getLogger().info("--------------------------------------------------");
                    getLogger().info("   " + moduleDescription.getName() + " v" + moduleDescription.getVersion() + " registered their ART.");
                    getLogger().info("");

                    ArtBuilder.Result createdART = art.build();
                    registerArtObjects(createdART);
                    registerGlobalFilters(createdART);

                    getLogger().info("");

                    getLogger().info("--------------------------------------------------");
                    getLogger().info("");
                })
                .accept(artBuilder);
    }

    @Override
    public ArtResult load(ArtConfig config) {
        try {
            if (!getParser().containsKey(config.getParser())) {
                throw new ArtParseException("Config " + config + " requires an unknown parser of type " + config.getParser());
            }

            return getParser().get(config.getParser()).get().parse(config);
        } catch (ArtParseException e) {
            logger.severe("ERROR in " + ConfigUtil.getFileName(config.getId()).orElse("unknown config") + ":");
            logger.severe("  --> " + e.getMessage());
            return DefaultArtResult.empty();
        }
    }

    @Override
    public <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
    }

    @Override
    public <TTarget> void addGlobalFilter(Class<TTarget> targetClass, ArtResultFilter<TTarget> filter) {
        if (!globalFilters.containsKey(targetClass)) {
            globalFilters.put(targetClass, new ArrayList<>());
        }
        globalFilters.get(targetClass).add(filter);
    }

    void registerActions(Map<String, ActionFactory<?, ?>> actions) {

        actions().register(actions);
        getLogger().info("   " + actions.size() + "x Action(s):");
        actions.keySet().forEach(actionIdentifier -> getLogger().info("    - " + actionIdentifier));
        getLogger().info("");
    }

    void registerRequirements(Map<String, RequirementFactory<?, ?>> requirements) {

        requirements().register(requirements);
        getLogger().info("   " + requirements.size() + "x Requirement(s):");
        requirements.keySet().forEach(actionIdentifier -> getLogger().info("    - " + actionIdentifier));
        getLogger().info("");
    }

    private void registerArtObjects(ArtBuilder.Result createdART) {
        for (Map.Entry<Class<?>, Map<String, ArtFactory<?, ?, ?, ?>>> entry : createdART.getFactories().entrySet()) {
            if (entry.getKey() == ActionFactory.class) {
                registerActions(entry.getValue().entrySet().stream().collect(toMap(Map.Entry::getKey, artFactory -> (ActionFactory<?, ?>) artFactory.getValue())));
            } else if (entry.getKey() == RequirementFactory.class) {
                registerRequirements(entry.getValue().entrySet().stream().collect(toMap(Map.Entry::getKey, artFactory -> (RequirementFactory<?, ?>) artFactory.getValue())));
            }
        }
    }

    private void registerGlobalFilters(ArtBuilder.Result createdART) {
        for (Map.Entry<Class<?>, List<ArtResultFilter<?>>> entry : createdART.getFilters().entrySet()) {
            if (!globalFilters.containsKey(entry.getKey())) {
                globalFilters.put(entry.getKey(), new ArrayList<>());
            }
            globalFilters.get(entry.getKey()).addAll(entry.getValue());

            getLogger().info("   " + entry.getValue().size() + "x " + entry.getKey().getName() + " Filter(s)");
        }
    }
}
