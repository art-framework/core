package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.conf.ActionConfig;

import java.util.Collection;
import java.util.Map;

public class DefaultActionFactory<TTarget> extends AbstractArtFactory<TTarget, ActionContext<TTarget>, Action<TTarget>> implements ActionFactory<TTarget> {

    public DefaultActionFactory(
            @NonNull Configuration configuration,
            @NonNull Class<TTarget> targetClass,
            @NonNull Class<Action<TTarget>> actionClass
    ) {
        super(configuration, targetClass, actionClass);
    }

    public DefaultActionFactory(
            @NonNull Configuration configuration,
            @NonNull Class<TTarget> targetClass,
            @NonNull Class<Action<TTarget>> actionClass,
            @NonNull ArtObjectProvider<Action<TTarget>> artObjectProvider
    ) {
        super(configuration, targetClass, actionClass, artObjectProvider);
    }

    @Override
    public ActionContext<TTarget> create(Map<ConfigMapType, ConfigMap> configMaps) {
        ActionConfig actionConfig = new ActionConfig();
        if (configMaps.containsKey(ConfigMapType.ART_CONFIG)) {
            actionConfig = configMaps.get(ConfigMapType.ART_CONFIG).applyTo(actionConfig);
        }
        return ActionContext.of(
                configuration(),
                getTargetClass(),
                createArtObject(configMaps.get(ConfigMapType.ART_OBJECT_CONFIG)),
                actionConfig
        );
    }
}
