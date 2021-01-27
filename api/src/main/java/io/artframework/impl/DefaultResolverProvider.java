package io.artframework.impl;

import io.artframework.AbstractProvider;
import io.artframework.ConfigurationException;
import io.artframework.Resolver;
import io.artframework.ResolverFactory;
import io.artframework.ResolverProvider;
import io.artframework.Scope;
import io.artframework.util.ReflectionUtil;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log(topic = "art-framework")
public class DefaultResolverProvider extends AbstractProvider implements ResolverProvider {

    // resolved type class -> resolver class -> factory map
    private final Map<Class<?>, Map<Class<?>, ResolverFactory<?>>> resolvers = new HashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public DefaultResolverProvider(Scope scope) {
        super(scope);

        ResolverProvider.DEFAULT.forEach(aClass -> add((Class) aClass));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Class<? extends Resolver<?>>> all() {

        return resolvers.values().stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .map(aClass -> (Class<Resolver<?>>) aClass)
                .collect(Collectors.toUnmodifiableList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <TType> Optional<ResolverFactory<TType>> get(Class<TType> type) {

        return ReflectionUtil.getEntryForTargetClass(type, resolvers)
                .map(Map::values)
                .flatMap(resolverFactories -> resolverFactories.stream().findFirst())
                .map(resolverFactory -> (ResolverFactory<TType>) resolverFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <TType> Optional<ResolverFactory<TType>> get(Class<TType> type, Class<? extends Resolver<?>> resolverClass) {

        return ReflectionUtil.getEntryForTargetClass(type, resolvers)
                .flatMap(classResolverFactoryMap -> ReflectionUtil.getEntryForTargetClass(resolverClass, classResolverFactoryMap))
                .map(resolverFactory -> (ResolverFactory<TType>) resolverFactory);
    }

    @Override
    public <TResolver extends Resolver<TType>, TType> ResolverProvider add(Class<TResolver> resolverClass) {

        ReflectionUtil.getInterfaceTypeArgument(resolverClass, Resolver.class, 0)
                .ifPresent(typeClass -> {
                    try {
                        resolvers.computeIfAbsent(typeClass, aClass -> new HashMap<>())
                                .putIfAbsent(resolverClass, ResolverFactory.of(scope(), resolverClass));
                        log.info("registered resolver " + resolverClass.getCanonicalName() + " for type: " + typeClass.getCanonicalName());
                    } catch (ConfigurationException e) {
                        log.severe("failed to register Resolver " + resolverClass.getCanonicalName() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                });

        return this;
    }

    @Override
    public <TResolver extends Resolver<TType>, TType> ResolverProvider add(Class<TResolver> resolverClass, Supplier<TResolver> supplier) {

        ReflectionUtil.getInterfaceTypeArgument(resolverClass, Resolver.class, 0)
                .ifPresent(typeClass -> {
                    try {
                        resolvers.computeIfAbsent(typeClass, aClass -> new HashMap<>())
                                .putIfAbsent(resolverClass, ResolverFactory.of(scope(), resolverClass, supplier));
                    } catch (ConfigurationException e) {
                        log.severe("failed to register Resolver " + resolverClass.getCanonicalName() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                });

        return this;
    }
}
