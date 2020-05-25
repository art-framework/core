package net.silthus.art.builder;

import lombok.RequiredArgsConstructor;
import net.silthus.art.ARTObject;
import net.silthus.art.api.ARTFactory;
import net.silthus.art.api.ARTObjectRegistrationException;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.requirements.RequirementFactory;

import java.util.*;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toMap;

public class ARTBuilder {

    private final Logger logger = Logger.getLogger("ARTBuilder");
    private final Map<Class<?>, TargetBuilder<?>> builders = new HashMap<>();

    /**
     * Collects all registered {@link ARTObject}s and their corresponding {@link ARTFactory}.
     * Then calls {@link ARTFactory#initialize()} on all collected factories to generate the corresponding {@link ARTFactory#getIdentifier()}.
     * <br>
     * If an {@link ARTObject} is invalid, e.g. has no name a log message will be output and the object filtered out.
     * <br>
     * Then the unique identifier of each object will be mapped to its factory and returned.
     * If a duplicate identifier is found, only the first object will be registered and a log message written.
     *
     * @return identifier to factory mapping
     */
    @SuppressWarnings("rawtypes")
    public Map<String, ARTFactory> build() {

        return builders.values().stream()
                .flatMap(targetBuilder -> targetBuilder.artFactories.stream())
                .filter(Objects::nonNull)
                .map(artFactory -> {
                    try {
                        artFactory.initialize();
                        return artFactory;
                    } catch (ARTObjectRegistrationException e) {
                        logger.severe(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(toMap(ARTFactory::getIdentifier, artFactory -> artFactory, (artFactory, artFactory2) -> {
                    // we got a duplicate identifier
                    logger.warning(String.format("Duplicate identifier \"%1$s\" in %2$s and %3$s detected. Only %2$s will be registered.",
                                artFactory.getIdentifier(),
                                artFactory.getArtObject().getClass().getCanonicalName(),
                                artFactory2.getArtObject().getClass().getCanonicalName()
                            )
                    );
                    return artFactory;
                }));
    }

    @SuppressWarnings("unchecked")
    public <TTarget> TargetBuilder<TTarget> target(Class<TTarget> targetClass) {

        if (!builders.containsKey(targetClass)) {
            builders.put(targetClass, new TargetBuilder<>(targetClass));
        }

        return (TargetBuilder<TTarget>) builders.get(targetClass);
    }

    public <TTarget, TConfig> TargetBuilder<TTarget>.FactoryBuilder<TConfig> action(Class<TTarget> targetClass, Class<TConfig> configClass, Action<TTarget, TConfig> action) {
        return target(targetClass).action(configClass, action);
    }

    public <TTarget, TConfig> TargetBuilder<TTarget>.FactoryBuilder<TConfig> requirement(Class<TTarget> targetClass, Class<TConfig> configClass, Requirement<TTarget, TConfig> requirement) {
        return target(targetClass).requirement(configClass, requirement);
    }

    @RequiredArgsConstructor
    public class TargetBuilder<TTarget> {

        private final Class<TTarget> targetClass;
        @SuppressWarnings("rawtypes")
        private final List<ARTFactory> artFactories = new ArrayList<>();

        public <TConfig> FactoryBuilder<TConfig> action(Class<TConfig> configClass, Action<TTarget, TConfig> action) {
            FactoryBuilder<TConfig> factoryBuilder = new FactoryBuilder<>(configClass, action);
            factoryBuilder.getArtFactory().ifPresent(artFactories::add);
            return factoryBuilder;
        }

        public <TConfig> FactoryBuilder<TConfig> requirement(Class<TConfig> configClass, Requirement<TTarget, TConfig> requirement) {
            FactoryBuilder<TConfig> factoryBuilder = new FactoryBuilder<>(configClass, requirement);
            factoryBuilder.getArtFactory().ifPresent(artFactories::add);
            return factoryBuilder;
        }

        public class FactoryBuilder<TConfig> {

            @SuppressWarnings("rawtypes")
            private final ARTFactory artFactory;

            public FactoryBuilder(Class<TConfig> configClass, ARTObject<TTarget, TConfig> artObject) {
                if (artObject instanceof Action) {
                    this.artFactory = new ActionFactory<>(targetClass, configClass, (Action<TTarget, TConfig>) artObject);
                } else if (artObject instanceof Requirement) {
                    this.artFactory = new RequirementFactory<>(targetClass, configClass, (Requirement<TTarget, TConfig>) artObject);
                } else {
                    this.artFactory = null;
                    logger.warning(String.format("%s is not a valid Action or Requirement. Make sure you implement the right interface.", artObject.getClass().getCanonicalName()));
                }
            }

            @SuppressWarnings("rawtypes")
            public Optional<ARTFactory> getArtFactory() {
                return Optional.ofNullable(artFactory);
            }

            public <TNextTarget> TargetBuilder<TNextTarget> target(Class<TNextTarget> targetClass) {
                return ARTBuilder.this.target(targetClass);
            }

            public <TNextConfig> FactoryBuilder<TNextConfig> action(Class<TNextConfig> configClass, Action<TTarget, TNextConfig> action) {
                return TargetBuilder.this.action(configClass, action);
            }

            public <TNextConfig> FactoryBuilder<TNextConfig> requirement(Class<TNextConfig> configClass, Requirement<TTarget, TNextConfig> requirement) {
                return TargetBuilder.this.requirement(configClass, requirement);
            }

            public FactoryBuilder<TConfig> withName(String name) {
                getArtFactory().ifPresent(artFactory -> artFactory.setIdentifier(name));
                return this;
            }
        }
    }
}

