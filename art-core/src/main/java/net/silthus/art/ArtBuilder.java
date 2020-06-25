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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.silthus.art.api.ArtObject;
import net.silthus.art.api.ArtObjectRegistrationException;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.factory.ArtFactory;
import net.silthus.art.api.parser.ArtResultFilter;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.requirements.RequirementFactory;

import javax.annotation.concurrent.Immutable;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class ArtBuilder {

    private final Logger logger = Logger.getLogger("ARTBuilder");
    private final Map<Class<?>, TargetBuilder<?>> builders = new HashMap<>();

    ArtBuilder() {
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

        Map<Class<?>, Map<String, ArtFactory<?, ?, ?, ?>>> factories = builders.values().stream()
                .flatMap(targetBuilder -> targetBuilder.artFactories.stream())
                .filter(Objects::nonNull)
                .map(artFactory -> {
                    try {
                        artFactory.initialize();
                        return artFactory;
                    } catch (ArtObjectRegistrationException e) {
                        logger.severe(e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                // TODO: refactor to group by instance of same type
                .collect(groupingBy(ArtFactory::getClass, toMap(ArtFactory::getIdentifier, artFactory -> artFactory, (artFactory, artFactory2) -> {
                    // we got a duplicate identifier
                    logger.warning(String.format("Duplicate identifier \"%1$s\" in %2$s and %3$s detected. Only %2$s will be registered.",
                            artFactory.getIdentifier(),
                            artFactory.getArtObject().getClass().getCanonicalName(),
                            artFactory2.getArtObject().getClass().getCanonicalName()
                            )
                    );
                    return artFactory;
                })));

        Map<Class<?>, List<ArtResultFilter<?>>> filters = builders.values().stream()
                .collect(toMap(builder -> builder.targetClass,
                        builder -> builder.globalFilters.stream().map(artResultFilter -> (ArtResultFilter<?>) artResultFilter).collect(Collectors.toList())));

        return new Result(factories, filters);
    }

    @SuppressWarnings("unchecked")
    public <TTarget> TargetBuilder<TTarget> target(Class<TTarget> targetClass) {

        if (!builders.containsKey(targetClass)) {
            builders.put(targetClass, new TargetBuilder<>(targetClass));
        }

        return (TargetBuilder<TTarget>) builders.get(targetClass);
    }

    public <TTarget, TConfig> TargetBuilder<TTarget>.FactoryBuilder action(Class<TTarget> targetClass, Action<TTarget, TConfig> action) {
        return target(targetClass).action(action);
    }

    public <TTarget, TConfig> TargetBuilder<TTarget>.FactoryBuilder requirement(Class<TTarget> targetClass, Requirement<TTarget, TConfig> requirement) {
        return target(targetClass).requirement(requirement);
    }

    @RequiredArgsConstructor
    public class TargetBuilder<TTarget> {

        private final Class<TTarget> targetClass;
        private final List<ArtFactory<TTarget, ?, ?, ?>> artFactories = new ArrayList<>();
        private final List<ArtResultFilter<TTarget>> globalFilters = new ArrayList<>();

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

        public TargetBuilder<TTarget> globalFilter(ArtResultFilter<TTarget> filter) {
            globalFilters.add(filter);
            return this;
        }

        public <NewTarget> TargetBuilder<NewTarget> target(Class<NewTarget> newTargetClass) {
            return ArtBuilder.this.target(newTargetClass);
        }

        public class FactoryBuilder {

            private final ArtFactory<TTarget, ?, ?, ?> artFactory;

            @SuppressWarnings("unchecked")
            public FactoryBuilder(ArtObject artObject) {
                if (artObject instanceof Action) {
                    this.artFactory = new ActionFactory<>(targetClass, (Action<TTarget, ?>) artObject);
                } else if (artObject instanceof Requirement) {
                    this.artFactory = new RequirementFactory<>(targetClass, (Requirement<TTarget, ?>) artObject);
                } else {
                    this.artFactory = null;
                    logger.warning(String.format("%s is not a valid Action or Requirement. Make sure you implement the right interface.", artObject.getClass().getCanonicalName()));
                }
            }

            public Optional<ArtFactory<TTarget, ?, ?, ?>> getArtFactory() {
                return Optional.ofNullable(artFactory);
            }

            public <TNextTarget> TargetBuilder<TNextTarget> target(Class<TNextTarget> targetClass) {
                return ArtBuilder.this.target(targetClass);
            }

            public <TNextConfig> FactoryBuilder action(Action<TTarget, TNextConfig> action) {
                return TargetBuilder.this.action(action);
            }

            public <TNextTarget, TNextConfig> TargetBuilder<TNextTarget>.FactoryBuilder action(Class<TNextTarget> targetClass, Action<TNextTarget, TNextConfig> requirement) {
                return ArtBuilder.this.action(targetClass, requirement);
            }

            public <TNextConfig> FactoryBuilder requirement(Requirement<TTarget, TNextConfig> requirement) {
                return TargetBuilder.this.requirement(requirement);
            }

            public <TNextTarget, TNextConfig> TargetBuilder<TNextTarget>.FactoryBuilder requirement(Class<TNextTarget> targetClass, Requirement<TNextTarget, TNextConfig> requirement) {
                return ArtBuilder.this.requirement(targetClass, requirement);
            }

            public FactoryBuilder withName(String name) {
                getArtFactory().ifPresent(artFactory -> artFactory.setIdentifier(name));
                return this;
            }
        }
    }

    @Getter
    @Immutable
    static class Result {

        private final Map<Class<?>, Map<String, ArtFactory<?, ?, ?, ?>>> factories;
        private final Map<Class<?>, List<ArtResultFilter<?>>> filters;

        public Result(Map<Class<?>, Map<String, ArtFactory<?, ?, ?, ?>>> factories, Map<Class<?>, List<ArtResultFilter<?>>> filters) {
            this.factories = ImmutableMap.copyOf(factories);
            this.filters = ImmutableMap.copyOf(filters);
        }
    }
}

