package net.silthus.art;

import lombok.NonNull;
import net.silthus.art.impl.DefaultActionFactory;

public interface ActionFactory<TTarget> extends ArtFactory<ActionContext<TTarget>, Action<TTarget>>
{
    static <TTarget> ActionFactory<TTarget> of(
            @NonNull Configuration configuration,
            @NonNull Class<TTarget> targetClass,
            @NonNull Class<Action<TTarget>> actionClass
    ) {
        return new DefaultActionFactory<>(configuration, targetClass, actionClass);
    }

    static <TTarget> ActionFactory<TTarget> of(
            @NonNull Configuration configuration,
            @NonNull Class<TTarget> targetClass,
            @NonNull Class<Action<TTarget>> actionClass,
            @NonNull ArtObjectProvider<Action<TTarget>> artObjectProvider
    ) {
        return new DefaultActionFactory<>(configuration, targetClass, actionClass, artObjectProvider);
    }
}
