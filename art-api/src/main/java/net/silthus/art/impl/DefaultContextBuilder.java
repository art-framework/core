package net.silthus.art.impl;

import net.silthus.art.Configuration;
import net.silthus.art.Context;
import net.silthus.art.ContextBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DefaultContextBuilder implements ContextBuilder {

    private final Configuration configuration;

    public DefaultContextBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public ContextBuilder load(File file) {
        return null;
    }

    @Override
    public ContextBuilder load(Map<String, Object> map) {
        return null;
    }

    @Override
    public ContextBuilder load(List<String> list) {
        return null;
    }

    @Override
    public Context build() {
        return null;
    }
}
