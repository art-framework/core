package net.silthus.art.actions;

import lombok.Data;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.parser.ArtParser;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;

@Data
@Singleton
public class DefaultActionManager implements ActionManager {

    private final Map<String, Provider<ArtParser>> parser;
    private final Map<String, ActionFactory<?, ?>> actionFactories = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Inject
    private Logger logger;

    @Inject
    public DefaultActionManager(Map<String, Provider<ArtParser>> parser) {
        this.parser = parser;
    }

    @Override
    public boolean exists(String identifier) {
        return actionFactories.containsKey(identifier);
    }

    @Override
    public void register(Map<String, ActionFactory<?, ?>> actionFactories) {
        for (Map.Entry<String, ActionFactory<?, ?>> entry : actionFactories.entrySet()) {
            if (exists(entry.getKey())) {
                getLogger().warning("duplicate actions detected for identifier " + entry.getKey() + ": " + entry.getValue().getArtObject().getClass().getCanonicalName() + " <--> " + actionFactories.get(entry.getKey()).getArtObject().getClass().getCanonicalName());
                getLogger().warning("not registering: " + entry.getValue().getArtObject().getClass().getCanonicalName());
            } else {
                this.actionFactories.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Optional<ActionFactory<?, ?>> getFactory(String identifier) {
        return Optional.ofNullable(actionFactories.get(identifier));
    }
}
