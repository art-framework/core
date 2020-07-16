package net.silthus.art;

import lombok.NonNull;
import net.silthus.art.impl.DefaultTriggerFactory;

public interface TriggerFactory extends ArtFactory<TriggerContext, Trigger>
{
    static TriggerFactory of(
            @NonNull Configuration configuration,
            @NonNull ArtObjectInformation<Trigger> information
    ) {
        return new DefaultTriggerFactory(configuration, information);
    }
}
