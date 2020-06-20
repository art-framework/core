package net.silthus.art.api.actions;

import net.silthus.art.api.ActionContext;
import net.silthus.art.api.ArtFactory;
import net.silthus.art.api.config.ArtObjectConfig;

/**
 * The {@link ActionFactory} creates a fresh {@link ActionContext} for each unique
 * configuration of the registered {@link Action}s.
 * <br>
 * One {@link ActionFactory} is created per target type and {@link Action}.
 *
 * @param <TTarget> target type this factory accepts.
 * @param <TConfig> custom action config type used when creating the {@link ActionContext}.
 */
public class ActionFactory<TTarget, TConfig> extends ArtFactory<TTarget, TConfig, Action<TTarget, TConfig>> {

    public ActionFactory(Class<TTarget> targetClass, Action<TTarget, TConfig> action) {
        super(targetClass, action);
    }

    @Override
    public ActionContext<TTarget, TConfig> create(ArtObjectConfig<TConfig> config) {
        return new ActionContext<>(getTargetClass(), getArtObject(), (ActionConfig<TConfig>) config);
    }
}
