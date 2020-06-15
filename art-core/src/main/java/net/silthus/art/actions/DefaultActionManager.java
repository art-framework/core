package net.silthus.art.actions;

import lombok.Data;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.parser.ARTParseException;
import net.silthus.art.api.parser.ARTParser;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
@Singleton
public class DefaultActionManager implements ActionManager {

    private final Set<ARTParser> parser;
    private final Map<String, ActionFactory<?, ?>> actionFactories = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Inject
    private Logger logger;

    @Inject
    public DefaultActionManager(Set<ARTParser> parser) {
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

    @Override
    public List<ActionContext<?, ?>> create(ARTConfig config) {

        try {
            List<ARTParser> parsers = parser.stream()
                    .filter(parser -> parser.matches(config))
                    .collect(Collectors.toList());

            if (parsers.size() > 1) {
                throw new ARTParseException("Multiple parsers matched the config with id " + config.getId());
            } else if (parsers.isEmpty()) {
                throw new ARTParseException("No parser matched the config with id " + config.getId());
            }

            return parsers.get(0).parseActions(config);
        } catch (ARTParseException e) {
            logger.warning(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> List<ActionContext<TTarget, ?>> create(Class<TTarget> targetClass, ARTConfig config) {

        return create(config).stream()
                .filter(action -> action.getTargetClass().equals(targetClass))
                .map(actionContext -> (ActionContext<TTarget, ?>) actionContext)
                .collect(Collectors.toList());
    }
}
