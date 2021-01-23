package io.artframework.impl;

import io.artframework.*;
import io.artframework.util.ReflectionUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultResolverProvider extends AbstractProvider implements ResolverProvider {

    private final Map<Class<?>, Map<Class<? extends Resolver<?>>, ResolverFactory<?>>> resolvers = new HashMap<>();

    public DefaultResolverProvider(Scope scope) {
        super(scope);
    }

    @Override
    public Collection<Class<? extends Resolver<?>>> all() {

        return resolvers.values().stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
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

    @Override
    public <TResolver extends Resolver<TType>, TType> ResolverProvider add(Class<TResolver> resolverClass) {

        ReflectionUtil.getInterfaceTypeArgument(resolverClass, Resolver.class, 0)
                .ifPresent(typeClass -> resolvers.computeIfAbsent(typeClass, aClass -> new HashMap<>())
                        .putIfAbsent(resolverClass, ResolverFactory.of(resolverClass)));

        return this;
    }

    @Override
    public <TResolver extends Resolver<TType>, TType> ResolverProvider add(Class<TResolver> resolverClass, Supplier<TResolver> supplier) {
        return null;
    }
}
