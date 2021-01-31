package io.artframework.impl;

import io.artframework.AbstractProvider;
import io.artframework.Replacement;
import io.artframework.ReplacementProvider;
import io.artframework.Scope;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.*;

@Log(topic = "art-framework")
public class DefaultReplacementProvider extends AbstractProvider implements ReplacementProvider {

    private final Map<Class<?>, Replacement> replacements = new HashMap<>();

    public DefaultReplacementProvider(Scope scope) {

        super(scope);
        Arrays.stream(DEFAULTS).forEach(this::add);
    }

    @Override
    public Collection<Replacement> all() {

        return List.copyOf(replacements.values());
    }

    @Override
    public ReplacementProvider add(@NonNull Replacement replacement) {

        if (replacements.containsKey(replacement.getClass())) {
            log.warning("not registering duplicate replacement: " + replacement.getClass().getCanonicalName());
            return this;
        }

        replacements.put(replacement.getClass(), replacement);
        log.info("[REGISTERED] replacement: " + replacement.getClass().getCanonicalName());

        return this;
    }

    @Override
    public ReplacementProvider remove(@NonNull Class<? extends Replacement> replacement) {

        replacements.remove(replacement);

        return this;
    }

    @Override
    public ReplacementProvider removeAll() {

        replacements.clear();

        return this;
    }
}
