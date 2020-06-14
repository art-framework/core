package net.silthus.art.api.actions;

import net.silthus.art.api.config.ARTConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class NullActionManager implements ActionManager {

    @Override
    public Optional<ActionFactory<?, ?>> getFactory(String identifier) {
        return Optional.empty();
    }

    @Override
    public List<Action<?, ?>> create(ARTConfig config) {
        return new ArrayList<>();
    }

    @Override
    public <TTarget> List<Action<TTarget, ?>> create(Class<TTarget> targetClass, ARTConfig config) {
        return new ArrayList<>();
    }
}
