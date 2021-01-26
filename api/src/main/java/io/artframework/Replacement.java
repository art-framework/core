package io.artframework;

import io.artframework.impl.ReplacementContext;

/**
 * Replacements are applied to {@link ConfigMap}s before resolving and applying values.
 * <p>Register your replacement with the {@link ReplacementProvider}.
 */
@FunctionalInterface
public interface Replacement {

    /**
     * Replaces the given string with a new value based on the resolution context.
     * <p>Simply return the input value if no replacement is performed.
     *
     * @param value the value that should be replaced
     * @param context the context of the replacement
     * @return the new value or the unmodified if no replacement was performed
     */
    String replace(String value, ReplacementContext context);
}
