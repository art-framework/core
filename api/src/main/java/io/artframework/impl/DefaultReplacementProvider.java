package io.artframework.impl;

import io.artframework.AbstractProvider;
import io.artframework.Replacement;
import io.artframework.ReplacementProvider;
import io.artframework.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DefaultReplacementProvider extends AbstractProvider implements ReplacementProvider {

    private final List<Replacement> replacements = new ArrayList<>();

    public DefaultReplacementProvider(Scope scope) {

        super(scope);
        Arrays.stream(DEFAULTS).forEach(this::add);
    }

    @Override
    public Collection<Replacement> all() {

        return List.copyOf(replacements);
    }

    @Override
    public ReplacementProvider add(Replacement replacement) {

        replacements.add(replacement);

        return this;
    }

    @Override
    public ReplacementProvider remove(Replacement replacement) {

        replacements.remove(replacement);

        return this;
    }

    @Override
    public ReplacementProvider removeAll() {

        replacements.clear();

        return this;
    }
}
