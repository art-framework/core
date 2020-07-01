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

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.silthus.art.api.Action;
import net.silthus.art.api.ArtObject;
import net.silthus.art.api.Requirement;
import net.silthus.art.api.Trigger;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.api.parser.ArtResultFilter;
import net.silthus.art.api.requirements.RequirementFactory;
import net.silthus.art.api.trigger.Target;
import net.silthus.art.api.trigger.TriggerManager;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class ArtBuilder {

    private final Logger logger = Logger.getLogger("ARTBuilder");
    private final Map<Class<?>, TargetBuilder<?>> builders = new HashMap<>();

    @Getter(AccessLevel.PRIVATE)
    private final ActionManager actionManager;
    @Getter(AccessLevel.PRIVATE)
    private final TriggerManager triggerManager;

    @Inject
    ArtBuilder(ActionManager actionManager, TriggerManager triggerManager) {
        this.actionManager = actionManager;
        this.triggerManager = triggerManager;
    }

    /**
     * Collects all registered {@link ArtObject}s and their corresponding {@link ArtFactory} grouped by their class.
     * Then calls {@link ArtFactory#initialize()} on all collected factories to generate the corresponding identifier.
     * <br>
     * If an {@link ArtObject} is invalid, e.g. has no name a log message will be output and the object filtered out.
     * <br>
     * Then the unique identifier of each object will be mapped to its factory and returned.
     * If a duplicate identifier is found, only the first object will be registered and a log message written.
     *
     * @return identifier to factory mapping grouped by their class
     */
    Result build() {

        return new Result(buildFactories(), buildFilters(), buildTargetWrapper());
    }

    private Map<Class<?>, List<ArtFactory<?, ?, ?, ?>>> buildFactories() {
        return builders.values().stream()
                .flatMap(targetBuilder -> targetBuilder.artFactories.stream())
                .filter(Objects::nonNull)
                .collect(groupingBy(ArtFactory::getClass));
    }

    private Map<Class<?>, List<ArtResultFilter<?>>> buildFilters() {
        return builders.values().stream()
                .collect(toMap(builder -> builder.targetClass,
                        builder -> builder.globalFilters.stream()
                                .map(artResultFilter -> (ArtResultFilter<?>) artResultFilter)
                                .collect(Collectors.toList()))
                );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<Class<?>, Function<?, Target<?>>> buildTargetWrapper() {
        return builders.values().stream()
                .filter(targetBuilder -> Objects.nonNull(targetBuilder.targetWrapper))
                .collect(toMap(builder -> builder.targetClass,
                        builder -> (Function) builder.targetWrapper)
                );
    }

    @SuppressWarnings("unchecked")
    public <TTarget> TargetBuilder<TTarget> target(Class<TTarget> targetClass) {

        if (!builders.containsKey(targetClass)) {
            builders.put(targetClass, new TargetBuilder<>(targetClass));
        }

        return (TargetBuilder<TTarget>) builders.get(targetClass);
    }

    @RequiredArgsConstructor
    public class TargetBuilder<TTarget> {

        private final Class<TTarget> targetClass;
        private final List<ArtFactory<TTarget, ?, ?, ?>> artFactories = new ArrayList<>();
        private final List<ArtResultFilter<TTarget>> globalFilters = new ArrayList<>();
        private Function<TTarget, Target<TTarget>> targetWrapper;

        public <TConfig> FactoryBuilder action(Action<TTarget, TConfig> action) {
            FactoryBuilder factoryBuilder = new FactoryBuilder(action);
            factoryBuilder.getArtFactory().ifPresent(artFactories::add);
            return factoryBuilder;
        }

        public <TConfig> FactoryBuilder requirement(Requirement<TTarget, TConfig> requirement) {
            FactoryBuilder factoryBuilder = new FactoryBuilder(requirement);
            factoryBuilder.getArtFactory().ifPresent(artFactories::add);
            return factoryBuilder;
        }

        public FactoryBuilder trigger(Trigger trigger) {
            FactoryBuilder factoryBuilder = new FactoryBuilder(trigger);
            factoryBuilder.getArtFactory().ifPresent(artFactories::add);
            return factoryBuilder;
        }

        public TargetBuilder<TTarget> wrapper(Function<TTarget, Target<TTarget>> constructor) {
            targetWrapper = constructor;
            return this;
        }

        public TargetBuilder<TTarget> filter(ArtResultFilter<TTarget> filter) {
            globalFilters.add(filter);
            return this;
        }

        public <TNewTarget> TargetBuilder<TNewTarget> and(Class<TNewTarget> targetClass) {
            return ArtBuilder.this.target(targetClass);
        }

        public class FactoryBuilder {

            private final List<ArtFactory<TTarget, ?, ?, ? extends ArtObjectConfig<?>>> artFactories = new ArrayList<>();

            @SuppressWarnings("unchecked")
            public FactoryBuilder(ArtObject artObject) {
                if (artObject instanceof Action) {
                    this.artFactories.add(getActionManager().create(targetClass, (Action<TTarget, ?>) artObject));
                } else if (artObject instanceof Requirement) {
                    this.artFactories.add(RequirementFactory.of(targetClass, (Requirement<TTarget, ?>) artObject));
                } else if (artObject instanceof Trigger) {
                    this.artFactories.addAll(getTriggerManager().create((Trigger) artObject).stream()
                            .map(triggerFactory -> (ArtFactory<TTarget, ?, ?, ? extends ArtObjectConfig<?>>) triggerFactory)
                            .collect(Collectors.toList())
                    );
                } else {
                    logger.warning(String.format("%s is not a valid Action, Requirement or Trigger. Make sure you implement the right interface.", artObject.getClass().getCanonicalName()));
                }
            }

            private Optional<ArtFactory<TTarget, ?, ?, ? extends ArtObjectConfig<?>>> getArtFactory() {
                if (artFactories.size() == 1) {
                    return Optional.ofNullable(artFactories.get(0));
                }
                return Optional.empty();
            }

            public FactoryBuilder withName(String name) {
                getArtFactory().ifPresent(artFactory -> artFactory.setIdentifier(name));
                return this;
            }

            public FactoryBuilder withDescription(String... description) {
                getArtFactory().ifPresent(artFactory -> artFactory.setDescription(description));
                return this;
            }

            @SuppressWarnings("unchecked")
            public <TConfig> FactoryBuilder withConfig(Class<TConfig> configClass) {
                getArtFactory()
                        .map(factory -> (ArtFactory<TTarget, TConfig, ?, ? extends ArtObjectConfig<TConfig>>) factory)
                        .ifPresent(artFactory -> artFactory.setConfigClass(configClass));
                return this;
            }

            public TargetBuilder<TTarget> and() {
                return TargetBuilder.this;
            }

            public <TNewTarget> TargetBuilder<TNewTarget> and(Class<TNewTarget> targetClass) {
                return ArtBuilder.this.target(targetClass);
            }
        }
    }

    @Getter
    @Immutable
    static class Result {

        private final Map<Class<?>, List<ArtFactory<?, ?, ?, ?>>> factories;
        private final Map<Class<?>, List<ArtResultFilter<?>>> filters;
        private final Map<Class<?>, Function<?, Target<?>>> targetWrappers;

        public Result(Map<Class<?>, List<ArtFactory<?, ?, ?, ?>>> factories, Map<Class<?>, List<ArtResultFilter<?>>> filters, Map<Class<?>, Function<?, Target<?>>> targetWrappers) {
            this.factories = ImmutableMap.copyOf(factories);
            this.filters = ImmutableMap.copyOf(filters);
            this.targetWrappers = ImmutableMap.copyOf(targetWrappers);
        }
    }
}

