package io.artframework;

import io.artframework.impl.DefaultResolverProvider;
import io.artframework.resolver.EnumResolver;

import java.util.*;
import java.util.function.Supplier;

/**
 * The ResolverProvider provides a way to register and query {@link Resolver}s.
 */
public interface ResolverProvider extends Provider {

    List<Class<? extends Resolver<?>>> DEFAULT = Collections.singletonList(
            EnumResolver.class
    );

    static ResolverProvider of(Scope scope) {

        return new DefaultResolverProvider(scope);
    }

    /**
     * @return an immutable list of all registered resolvers
     */
    Collection<Class<? extends Resolver<?>>> all();

    /**
     * Tries to find a registered resolver for the given target type.
     * <p>Use the returned factory to create the resolver providing the config values.
     *
     * @param type the class of the type that should be resolved
     * @param <TType> the type that should be resolved
     * @return a factory for the given resolver type or an empty optional if none exists
     */
    <TType> Optional<ResolverFactory<TType>> get(Class<TType> type);

    /**
     * Directly gets the resolver registered for the given resolver class.
     *
     * @param type the class of the type that should be resolved
     * @param resolverClass the class of the resolver
     * @param <TType> the type the resolver resolves
     * @return the registered resolver factory or an empty optional if the resolver is not registered
     */
    <TType> Optional<ResolverFactory<TType>> get(Class<TType> type, Class<? extends Resolver<?>> resolverClass);

    /**
     * Registers the given resolver in this provider.
     * <p>The registration will fail and print a log message if the resolver is already registered.
     * <p>The resolver must have an parameterless public constructor or else {@link #add(Class, Supplier)} must be used.
     *
     * @param resolverClass the class of the resolver
     * @param <TResolver> the type of the resolver
     * @param <TType> the type that is resolved by the resolver
     * @return this provider
     */
    <TResolver extends Resolver<TType>, TType> ResolverProvider add(Class<TResolver> resolverClass);

    /**
     * Registers the given resolver with the provided method to create it in this provider.
     * <p>The registration will fail and print a log message if the resolver is already registered.
     * <p>Use the {@link #add(Class)} method if your resolver has a parameterless public constructor.
     *
     * @param resolverClass the class of the resolver
     * @param supplier the function that knows how to create the given resolver
     * @param <TResolver> the type of the resolver
     * @param <TType> the type that is resolved by the resolver
     * @return this provider
     */
    <TResolver extends Resolver<TType>, TType> ResolverProvider add(Class<TResolver> resolverClass, Supplier<TResolver> supplier);
}
