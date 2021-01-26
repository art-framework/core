package io.artframework;

import io.artframework.impl.DefaultReplacementProvider;
import io.artframework.impl.ReplacementContext;

import java.util.Collection;

public interface ReplacementProvider extends Provider {

    static ReplacementProvider of(Scope scope) {

        return new DefaultReplacementProvider(scope);
    }

    /**
     * @return all registered replacements
     */
    Collection<Replacement> all();

    /**
     * Adds the given replacement to the list of replacements.
     *
     * @param replacement the replacement to add
     * @return this replacement provider
     */
    ReplacementProvider add(Replacement replacement);

    /**
     * Tries to replace the given value using all registered replacements.
     *
     * @param value the value that should be replaced
     * @param context the context of the replacement
     * @return the replaced or original value
     */
    default String replace(String value, ReplacementContext context) {

        for (Replacement replacement : all()) {
            value = replacement.replace(value, context);
        }

        return value;
    }
}
