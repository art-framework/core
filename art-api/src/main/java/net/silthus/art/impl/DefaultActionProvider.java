package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.*;

import java.util.Collection;
import java.util.Objects;

public class DefaultActionProvider extends AbstractArtFactoryProvider<ActionFactory<?>> implements ActionProvider {

    protected DefaultActionProvider(Configuration configuration) {
        super(configuration);
    }

    @Override
    public ActionProvider add(@NonNull ArtObjectInformation<Action<?>> actionInformation) {
        addFactory(ActionFactory.of(configuration(), actionInformation.get()));
        return this;
    }

    @Override
    public ActionProvider add(@NonNull Class<? extends Action<?>> actionClass) {
        try {
            return add(Objects.requireNonNull(ArtObjectInformation.of(actionClass).get()));
        } catch (ArtObjectInformationException e) {
            // TODO: error handling
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public ActionProvider add(@NonNull String identifier, @NonNull GenericAction action) {

        return add(ArtObjectInformation.of(identifier, Object.class, action));
    }

    @Override
    public <TTarget> ActionProvider add(String identifier, Class<TTarget> targetClass, Action<TTarget> action) {
        return add(ArtObjectInformation.of(identifier, targetClass, action));
    }

    @Override
    public <TAction extends Action<TTarget>, TTarget> ActionProvider add(Class<TAction> actionClass, ArtObjectProvider<TAction> action) {
        try {
            return add(Objects.requireNonNull(ArtObjectInformation.of(actionClass, action).get()));
        } catch (ArtObjectInformationException e) {
            // TODO: error handling
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public ArtProvider addAll(Collection<ArtObjectInformation<?>> artObjects) {
        for (ArtObjectInformation<?> artObject : artObjects) {
            add(Objects.requireNonNull(artObject.get()));
        }
        return this;
    }
}
