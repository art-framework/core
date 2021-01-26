package io.artframework;

import io.artframework.conf.KeyValuePair;
import io.artframework.impl.DefaultResolveContext;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * The ResolutionContext holds additional information about the resolution of resolvers
 * and replacement of variables.
 * <p>Register your {@link Resolver} with the {@link ResolverProvider} and your {@link Replacement}
 * of variables with the {@link ReplacementProvider}.
 */
public interface ResolveContext extends Scoped {

    static ResolveContext of(@NonNull Scope scope,
                             @NonNull ConfigMap configMap,
                             @NonNull Class<?> type,
                             @NonNull List<KeyValuePair> configValues,
                             @Nullable Target<?> target,
                             @Nullable ExecutionContext<?> executionContext) {

        return new DefaultResolveContext(scope, configMap, type, configValues, target, executionContext);
    }

    /**
     * The config map holds additional information about the configuration
     * of the resolver class and maps the {@link #configValues()} to their fields.
     *
     * @return the config map of the resolver
     */
    ConfigMap configMap();

    /**
     * @return the raw list of config key value pairs created by the parser
     */
    List<KeyValuePair> configValues();

    /**
     * @return the class of the type that is resolved by the resolver
     */
    Class<?> type();

    /**
     * @return the optional target that is available for the resolution
     */
    Optional<Target<?>> target();

    /**
     * @return the optional execution context that is available for the resolution
     */
    Optional<ExecutionContext<?>> executionContext();
}
