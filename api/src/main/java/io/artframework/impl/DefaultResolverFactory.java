package io.artframework.impl;

import io.artframework.*;
import io.artframework.conf.KeyValuePair;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Supplier;

@Data
@Accessors(fluent = true)
public class DefaultResolverFactory<TType> implements ResolverFactory<TType> {

    private final Scope scope;
    private final Class<? extends Resolver<TType>> resolverClass;
    private final Supplier<? extends Resolver<TType>> supplier;
    private final ConfigMap configMap;

    public DefaultResolverFactory(Scope scope, Class<? extends Resolver<TType>> resolverClass,
                                  Supplier<? extends Resolver<TType>> supplier,
                                  ConfigMap configMap) {
        this.scope = scope;
        this.resolverClass = resolverClass;
        this.supplier = supplier;
        this.configMap = configMap;
    }

    @Override
    public Resolver<TType> create(List<KeyValuePair> configValues) throws ConfigurationException {

        return configMap().with(configValues).applyTo(scope(), supplier.get());
    }
}
