package net.silthus.art;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.silthus.art.api.ArtFactory;
import net.silthus.art.api.ArtObject;
import net.silthus.art.api.ArtObjectRegistrationException;
import net.silthus.art.api.ArtType;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.requirements.RequirementFactory;

import java.util.*;
import java.util.logging.Logger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class ArtBuilder {

    @Getter(AccessLevel.PACKAGE)
    private final String pluginName;
    private final Logger logger = Logger.getLogger("ARTBuilder");
    private final Map<Class<?>, TargetBuilder<?>> builders = new HashMap<>();

    public ArtBuilder(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * Collects all registered {@link ArtObject}s and their corresponding {@link ArtFactory} grouped by their {@link ArtType}.
     * Then calls {@link ArtFactory#initialize()} on all collected factories to generate the corresponding identifier.
     * <br>
     * If an {@link ArtObject} is invalid, e.g. has no name a log message will be output and the object filtered out.
     * <br>
     * Then the unique identifier of each object will be mapped to its factory and returned.
     * If a duplicate identifier is found, only the first object will be registered and a log message written.
     *
     * @return identifier to factory mapping grouped by the {@link ArtType}
     */
    @SuppressWarnings("rawtypes")
    Map<ArtType, Map<String, ArtFactory>> build() {

        return builders.values().stream()
                .flatMap(targetBuilder -> targetBuilder.artFactories.stream())
                .filter(Objects::nonNull)
                .map(artFactory -> {
                    try {
                        artFactory.initialize();
                        return artFactory;
                    } catch (ArtObjectRegistrationException e) {
                        logger.severe(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                // TODO: refactor to group by instance of same type
                .collect(groupingBy(ArtFactory::getARTType, toMap(ArtFactory::getIdentifier, artFactory -> artFactory, (artFactory, artFactory2) -> {
                    // we got a duplicate identifier
                    logger.warning(String.format("Duplicate identifier \"%1$s\" in %2$s and %3$s detected. Only %2$s will be registered.",
                            artFactory.getIdentifier(),
                            artFactory.getArtObject().getClass().getCanonicalName(),
                            artFactory2.getArtObject().getClass().getCanonicalName()
                            )
                    );
                    return artFactory;
                })));
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
        @SuppressWarnings("rawtypes")
        private final List<ArtFactory> artFactories = new ArrayList<>();

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

        public class FactoryBuilder {

            @SuppressWarnings("rawtypes")
            private final ArtFactory artFactory;

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

            @SuppressWarnings("rawtypes")
            public Optional<ArtFactory> getArtFactory() {
                return Optional.ofNullable(artFactory);
            }

            public <TNextTarget> TargetBuilder<TNextTarget> target(Class<TNextTarget> targetClass) {
                return ArtBuilder.this.target(targetClass);
            }

            public <TNextConfig> FactoryBuilder action(Action<TTarget, TNextConfig> action) {
                return TargetBuilder.this.action(action);
            }

            public <TNextConfig> FactoryBuilder requirement(Requirement<TTarget, TNextConfig> requirement) {
                return TargetBuilder.this.requirement(requirement);
            }

            public FactoryBuilder withName(String name) {
                getArtFactory().ifPresent(artFactory -> artFactory.setIdentifier(name));
                return this;
            }
        }
    }
}
