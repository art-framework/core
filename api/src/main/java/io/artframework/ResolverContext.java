package io.artframework;

import io.artframework.conf.KeyValuePair;

import java.util.List;

/**
 * The ResolverContext holds additional information about the resolution
 * and provides utility methods to help in the resolution of the target type.
 */
public interface ResolverContext {

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
}
