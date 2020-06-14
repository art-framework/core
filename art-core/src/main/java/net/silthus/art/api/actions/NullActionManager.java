package net.silthus.art.api.actions;

import net.silthus.art.api.config.ARTConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class NullActionManager implements ActionManager {

    @Override
    public boolean exists(String identifier) {
        return false;
    }

    @Override
    public void register(Map<String, ActionFactory<?, ?>> actionFactories) {
    }

    @Override
    public Optional<ActionFactory<?, ?>> getFactory(String identifier) {
        return Optional.empty();
    }

    @Override
    public List<ActionContext<?, ?>> create(ARTConfig config) {
        return new ArrayList<>();
    }

    @Override
    public <TTarget> List<ActionContext<TTarget, ?>> create(Class<TTarget> targetClass, ARTConfig config) {
        return new ArrayList<>();
    }
}
