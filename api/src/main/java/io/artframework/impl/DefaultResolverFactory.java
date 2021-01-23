package io.artframework.impl;

import io.artframework.Resolver;
import io.artframework.ResolverFactory;
import io.artframework.conf.ConfigFieldInformation;

import java.util.Map;

public class DefaultResolverFactory<TType> implements ResolverFactory<TType> {

    @Override
    public Resolver<TType> create(Map<ConfigFieldInformation, Object> configValues) {
        return null;
    }
}
