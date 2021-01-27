package io.artframework;

import io.artframework.impl.DefaultReplacementProvider;
import io.artframework.impl.ReplacementContext;
import io.artframework.replacements.VariableReplacement;

import java.util.Arrays;
import java.util.Collection;

/**
 * The replacement provider stores all registered replacements and
 * can be used to register custom replacements that are applied when a {@link ConfigMap} is resolved.
 * <p>The list of {@link #DEFAULTS} is added to the provider when it is created.
 * Use the {@link #removeDefaults()} method the remove the default replacements.
 */
public interface ReplacementProvider extends Provider {

    Replacement[] DEFAULTS = new Replacement[]{
            new VariableReplacement()
    };

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
     * Removes the given replacement from the provider.
     * <p>If the replacement is not registered nothing will happen.
     *
     * @param replacement the replacement that should be removed
     * @return this replacement provider
     */
    ReplacementProvider remove(Replacement replacement);

    /**
     * Removes all registered replacements from the provider.
     * <p>This will also remove the {@link #DEFAULTS} and if needed
     * they need to be added back in.
     *
     * @return this replacement provider
     */
    ReplacementProvider removeAll();

    /**
     * Removes all defaults from the replacement provider.
     *
     * @return this replacement provider
     */
    default ReplacementProvider removeDefaults() {

        Arrays.stream(DEFAULTS).forEach(this::remove);
        return this;
    }

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
