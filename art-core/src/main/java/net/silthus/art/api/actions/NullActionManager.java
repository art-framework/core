package net.silthus.art.api.actions;

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
}
