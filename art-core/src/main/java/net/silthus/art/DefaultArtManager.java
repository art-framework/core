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

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.Data;
import lombok.NonNull;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.ArtRegistrationException;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.api.factory.ArtFactoryRegistration;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.parser.ArtParser;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.Filter;
import net.silthus.art.api.requirements.RequirementFactory;
import net.silthus.art.api.requirements.RequirementManager;
import net.silthus.art.api.trigger.Target;
import net.silthus.art.api.trigger.TriggerContext;
import net.silthus.art.api.trigger.TriggerFactory;
import net.silthus.art.api.trigger.TriggerManager;
import net.silthus.art.util.ConfigUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static net.silthus.art.util.ReflectionUtil.getEntryForTarget;

@Data
@SuppressWarnings("rawtypes")
public class DefaultArtManager implements ArtManager {

    private final ActionManager actionManager;
    private final RequirementManager requirementManager;
    private final TriggerManager triggerManager;
    private final Provider<ArtBuilder> artBuilderProvider;

    @Inject
    private Logger logger = Logger.getLogger("ART");
    @Inject
    private Map<String, Provider<ArtParser>> parser = new HashMap<>();

    private final Map<Class<?>, List<Filter<?>>> globalFilters = new HashMap<>();
    private final Map<Class<?>, Function> targetWrapper = new HashMap<>();
    private final Map<ArtModuleDescription, ArtBuilder> registeredPlugins = new HashMap<>();

    @Inject
    public DefaultArtManager(ActionManager actionManager, RequirementManager requirementManager, TriggerManager triggerManager, Provider<ArtBuilder> artBuilderProvider) {
        this.actionManager = actionManager;
        this.requirementManager = requirementManager;
        this.triggerManager = triggerManager;
        this.artBuilderProvider = artBuilderProvider;
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
            artBuilder = getArtBuilderProvider().get();
        }

        builder.andThen(art -> registeredPlugins.put(moduleDescription, art))
                .andThen(art -> {
                    getLogger().info("--------------------------------------------------");
                    getLogger().info("   " + moduleDescription.getName() + " v" + moduleDescription.getVersion() + " registered their ART.");
                    getLogger().info("");

                    ArtBuilder.Result result = art.build();
                    registerArtObjects(result);
                    registerGlobalFilters(result);
                    registerTargetWrappers(result);

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

            ArtResult result = getParser().get(config.getParser()).get().parse(config);
            getLogger().info("Loaded ART config: " + ConfigUtil.getFileName(config.getId()));
            return result;
        } catch (ArtParseException e) {
            logger.severe("ERROR in " + ConfigUtil.getFileName(config.getId()).orElse("unknown config") + ":");
            logger.severe("  --> " + e.getMessage());
            return DefaultArtResult.empty();
        }
    }

    @Override
    public <TConfig> void trigger(String identifier, Predicate<TriggerContext<TConfig>> context, Target<?>... targets) {
        getTriggerManager().trigger(identifier, context, targets);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> Optional<Target<TTarget>> getTarget(@NonNull TTarget target) {

        Optional<Target<TTarget>> optionalTarget = getEntryForTarget(target, targetWrapper)
                .map(function -> (Target<TTarget>) function.apply(target));

        if (!optionalTarget.isPresent()) {
            getLogger().warning("Unable to find target wrapper for " + target.getClass().getCanonicalName() + "! " +
                    "Actions, Requirements and Trigger using this target type may silently fail.");
        }

        return optionalTarget;
    }

    <TFactory extends ArtFactory<?, ?, ?, ? extends ArtObjectConfig<?>>> void registerArtFactoryMap(String type, List<TFactory> factories, ArtFactoryRegistration<TFactory> factoryManager) {
        try {
            factoryManager.register(factories);
        } catch (ArtRegistrationException e) {
            getLogger().info("   " + e.getMessage());
        } finally {
            getLogger().info("   " + factories.size() + "x " + type + "(s) successfully registered:");
            factories.forEach(value -> getLogger().info("    - " + value.getIdentifier() + " " + value.getConfigString()));
            getLogger().info("");
        }
    }

    private void registerArtObjects(ArtBuilder.Result createdART) {
        for (Map.Entry<Class<?>, List<ArtFactory<?, ?, ?, ?>>> entry : createdART.getFactories().entrySet()) {
            if (entry.getKey() == ActionFactory.class) {
                registerArtFactoryMap("Action",
                        entry.getValue().stream().map(artFactory -> (ActionFactory<?, ?>) artFactory).collect(Collectors.toList()),
                        getActionManager()
                );
            } else if (entry.getKey() == RequirementFactory.class) {
                registerArtFactoryMap("Requirement",
                        entry.getValue().stream().map(artFactory -> (RequirementFactory<?, ?>) artFactory).collect(Collectors.toList()),
                        getRequirementManager()
                );
            } else if (entry.getKey() == TriggerFactory.class) {
                registerArtFactoryMap("Trigger",
                        entry.getValue().stream().map(artFactory -> (TriggerFactory<?>) artFactory).collect(Collectors.toList()),
                        getTriggerManager()
                );
            }
        }
    }

    private void registerGlobalFilters(ArtBuilder.Result createdART) {
        for (Map.Entry<Class<?>, List<Filter<?>>> entry : createdART.getFilters().entrySet()) {
            if (!globalFilters.containsKey(entry.getKey())) {
                globalFilters.put(entry.getKey(), new ArrayList<>());
            }
            globalFilters.get(entry.getKey()).addAll(entry.getValue());

            if (!entry.getValue().isEmpty()) {
                getLogger().info("   " + entry.getValue().size() + "x " + entry.getKey().getName() + " Filter(s)");
            }
        }
    }

    private void registerTargetWrappers(ArtBuilder.Result result) {
        for (Map.Entry<Class<?>, Function<?, Target<?>>> entry : result.getTargetWrappers().entrySet()) {
            if (targetWrapper.containsKey(entry.getKey())) {
                getLogger().warning("Found duplicate Target wrapper for target of type " + entry.getKey().getTypeName() + ". Only one will be used.");
                return;
            }
            targetWrapper.put(entry.getKey(), entry.getValue());

            getLogger().info("   Target wrapper for " + entry.getKey().getTypeName());
        }
    }
}
