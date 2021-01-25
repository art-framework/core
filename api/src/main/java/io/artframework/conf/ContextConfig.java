package io.artframework.conf;

import io.artframework.ConfigMap;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * The ContextConfig is a wrapper around the various {@link ConfigMap}s
 * a {@link io.artframework.ArtObjectContext} required when being created by an {@link io.artframework.Factory}.
 */
@Value
@Accessors(fluent = true)
public class ContextConfig {

    /**
     * The configuration used to create the config of the context.
     */
    ConfigMap contextConfig;
    /**
     * The configuration used when creating the art object.
     */
    ConfigMap artObjectConfig;
}
