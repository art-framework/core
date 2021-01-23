package io.artframework;

import io.artframework.conf.ConfigFieldInformation;
import io.artframework.impl.DefaultResolverFactory;

import java.util.Map;

/**
 * The ResolverFactory is responsible for creating new instances of the
 * given resolver type.
 * <p>One factory for each resolver is created automatically when registered with the {@link ResolverProvider}.
 *
 * @param <TType> the type that is resolved by the resolver
 */
public interface ResolverFactory<TType> {

    /**
     * Creates a new default resolver factory for the given resolver.
     *
     * @param resolverClass the class of the resolver to create a factory for
     * @param <TResolver> the type of the resolver
     * @param <TType> the type that gets resolved by the resolver
     * @return the created default resolver factory
     */
    static <TResolver extends Resolver<TType>, TType> ResolverFactory<TType> of(Class<TResolver> resolverClass) {

        return new DefaultResolverFactory<>();
    }

    /**
     * Creates a new instance of the resolver using the provided config values.
     *
     * @param configValues the config values to create the resolver with
     * @return the created resolver
     */
    Resolver<TType> create(Map<ConfigFieldInformation, Object> configValues);
}
