package net.silthus.art;

import net.silthus.art.api.ArtObjectRegistrationException;

import java.util.HashMap;
import java.util.Map;

public interface ArtFactory<TContext extends ArtObjectContext, TArtObject extends ArtObject> extends Provider {

    String getIdentifier();

    String[] getAlias();

    String[] getDescription();

    Class<TArtObject> getArtObjectClass();

    Class<?> getConfigClass();

    /**
     * Initializes the {@link ArtFactory}, loads all annotations and checks
     * if the {@link ArtObject} is configured correctly.
     * <br>
     * If everything looks good the {@link ArtObject} is registered for execution.
     * If not a {@link ArtObjectRegistrationException} is thrown.
     *
     * @throws ArtObjectRegistrationException if the {@link ArtObject} could not be registered.
     */
    void initialize() throws ArtObjectRegistrationException;

    default TContext create() {
        return create(new HashMap<>());
    }

    TContext create(Map<ConfigMapType, ConfigMap> configMaps);
}
