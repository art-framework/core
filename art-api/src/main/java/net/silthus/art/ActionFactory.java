package net.silthus.art;

import lombok.NonNull;
import net.silthus.art.impl.DefaultActionFactory;

public interface ActionFactory<TTarget> extends ArtFactory<ActionContext<TTarget>, Action<TTarget>>
{
    static <TTarget> ActionFactory<TTarget> of(
            @NonNull Configuration configuration,
            @NonNull ArtObjectInformation<Action<TTarget>> information
    ) {
        return new DefaultActionFactory<>(configuration, information);
    }
}
