package io.artframework;

import io.artframework.conf.KeyValuePair;
import io.artframework.impl.DefaultResolverContext;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * The ResolverContext holds additional information about the resolution
 * and provides utility methods to help in the resolution of the target type.
 */
public interface ResolverContext extends Scoped {

    static ResolverContext of(@NonNull Scope scope,
                              @NonNull ConfigMap configMap,
                              @NonNull List<KeyValuePair> configValues,
                              @Nullable Target<?> target,
                              @Nullable ExecutionContext<?> executionContext) {

        return new DefaultResolverContext(scope, configMap, configValues, target, executionContext);
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
     * @return the optional target that is available for the resolution
     */
    Optional<Target<?>> target();

    /**
     * @return the optional execution context that is available for the resolution
     */
    Optional<ExecutionContext<?>> executionContext();
}
