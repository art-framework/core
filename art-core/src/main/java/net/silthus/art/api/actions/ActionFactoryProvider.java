package net.silthus.art.api.actions;

import net.silthus.art.api.Action;

public interface ActionFactoryProvider {

    <TTarget, TConfig> ActionFactory<TTarget, TConfig> create(Class<TTarget> targetClass, Action<TTarget, TConfig> action);
}
