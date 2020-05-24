package net.silthus.art.api.actions;

import lombok.EqualsAndHashCode;
import net.silthus.art.api.ARTFactory;

/**
 * The {@link ActionFactory} creates a fresh {@link ActionContext} for each unique
 * configuration of the registered {@link Action}s.
 * <br>
 * One {@link ActionFactory} is created per target type and {@link Action}.
 *
 * @param <TTarget> target type this factory accepts.
 * @param <TConfig> custom action config type used when creating the {@link ActionContext}.
 */
@EqualsAndHashCode(callSuper = true)
public class ActionFactory<TTarget, TConfig> extends ARTFactory<TTarget, TConfig, Action<TTarget, TConfig>, ActionContext<TTarget, TConfig>, ActionConfig<TConfig>> {

    public ActionFactory(Class<TTarget> targetClass, Class<TConfig> configClass, Action<TTarget, TConfig> action) {
        super(targetClass, configClass, action);
    }

    public ActionContext<TTarget, TConfig> create(ActionConfig<TConfig> config) {
        return new ActionContext<>(getTargetClass(), getArtObject(), config);
    }
}
