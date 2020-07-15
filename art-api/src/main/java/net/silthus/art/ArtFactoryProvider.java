package net.silthus.art;

import java.util.Collection;
import java.util.Optional;

public interface ArtFactoryProvider<TFactory extends ArtFactory<?, ?>> extends Provider {

    boolean exists(String identifier);

    ArtFactoryProvider<TFactory> add(Collection<TFactory> factories);

    ArtFactoryProvider<TFactory> add(TFactory factory);

    Optional<TFactory> get(String identifier);
}
