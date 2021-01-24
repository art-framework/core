package io.artframework;

import io.artframework.conf.KeyValuePair;
import io.artframework.impl.DefaultResolverFactory;
import io.artframework.util.ConfigUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Supplier;

/**
 * The ResolverFactory is responsible for creating new instances of the
 * given resolver type.
 * <p>One factory for each resolver is created automatically when registered with the {@link ResolverProvider}.
 *
 * @param <TType> the type that is resolved by the resolver
 */
public interface ResolverFactory<TType> extends Scoped {

    /**
     * Creates a new default resolver factory for the given resolver.
     *
     * @param resolverClass the class of the resolver to create a factory for
     * @param <TResolver> the type of the resolver that gets registered
     * @param <TType> the type that gets resolved by the resolver
     * @return the created default resolver factory
     * @throws ConfigurationException if the resolver cannot be instantiated or if it has invalid {@link io.artframework.annotations.ConfigOption} annotations
     */
    static <TResolver extends Resolver<TType>, TType> ResolverFactory<TType> of(Scope scope, Class<TResolver> resolverClass) throws ConfigurationException {

        Supplier<TResolver> supplier = () -> {
            try {
                return resolverClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        };

        TResolver resolver = supplier.get();
        if (resolver == null) {
            throw new ConfigurationException("Unable to create an instance of " + resolverClass.getCanonicalName() + "! Make sure the resolver has a parameterless public constructor.");
        }

        return new DefaultResolverFactory<>(scope, resolverClass, supplier, ConfigMap.of(ConfigUtil.getConfigFields(resolverClass, resolver)));
    }

    /**
     * Creates a new resolver factory for the given resolver with the given supplier.
     *
     * @param resolverClass the class of the resolver to create a factory for
     * @param supplier the supplier that knows how to create the resolver
     * @param <TResolver> the type of the resolver
     * @param <TType> the type the resolver creates
     * @return the created factory
     * @throws ConfigurationException if the resolver has invalid {@link io.artframework.annotations.ConfigOption} annotations
     */
    static <TResolver extends Resolver<TType>, TType> ResolverFactory<TType> of(Scope scope, Class<TResolver> resolverClass, Supplier<TResolver> supplier) throws ConfigurationException {

        return new DefaultResolverFactory<>(scope, resolverClass, supplier, ConfigMap.of(ConfigUtil.getConfigFields(resolverClass, supplier.get())));
    }

    /**
     * @return the implementing class of the resolver
     */
    Class<? extends Resolver<TType>> resolverClass();

    /**
     * @return the config map that was created for the resolver
     */
    ConfigMap configMap();

    /**
     * Creates a new instance of the resolver using the provided config values.
     *
     * @param configValues the config values to create the resolver with
     * @return the created resolver
     * @throws ConfigurationException if the config values cannot be applied to the {@link ConfigMap} of the resolver
     */
    Resolver<TType> create(List<KeyValuePair> configValues) throws ConfigurationException;
}
