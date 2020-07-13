package net.silthus.art;

import java.net.URL;
import java.util.Optional;

public interface ArtObjectInformation {

    String getName();

    String getDescription();

    String[] getAlias();

    Optional<Class<?>> getConfigClass();

    Class<? extends ArtObject> getArtObjectClass();

    URL getLocation();
}
