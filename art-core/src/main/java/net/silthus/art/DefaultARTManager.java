package net.silthus.art;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.silthus.art.api.ARTFactory;
import net.silthus.art.api.ARTManager;
import net.silthus.art.api.ARTType;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.parser.ARTParseException;
import net.silthus.art.api.parser.ARTParser;
import net.silthus.art.api.parser.ARTResult;
import net.silthus.art.api.requirements.RequirementFactory;
import net.silthus.art.api.trigger.TriggerContext;
import org.apache.commons.lang3.NotImplementedException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toMap;

@Data
public class DefaultARTManager implements ARTManager {

    private final ActionManager actionManager;

    @Inject
    private Logger logger;
    private final Map<String, Provider<ARTParser>> parser;
    private final Map<String, ARTBuilder> registeredPlugins = new HashMap<>();

    @Inject
    public DefaultARTManager(ActionManager actionManager, Map<String, Provider<ARTParser>> parser) {
        this.actionManager = actionManager;
        this.parser = parser;
    }

    @Setter(AccessLevel.PACKAGE)
    private boolean loaded = false;

    @Override
    public void load() {

        setLoaded(true);
        getLogger().info("--- ART MANAGER LOADED ---");
    }

    @Override
    public void unload() {

        setLoaded(false);
        getLogger().info("--- ART MANAGER UNLOADED ---");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void register(String pluginName, Consumer<ARTBuilder> builder) {

        ARTBuilder artBuilder;
        if (registeredPlugins.containsKey(pluginName)) {
            artBuilder = registeredPlugins.get(pluginName);
        } else {
            artBuilder = new ARTBuilder(pluginName);
        }

        builder.andThen(art -> registeredPlugins.put(pluginName, art))
                .andThen(art -> {
                    getLogger().info(pluginName + " plugin registered their ART:");

                    Map<ARTType, Map<String, ARTFactory>> createdART = art.build();
                    for (Map.Entry<ARTType, Map<String, ARTFactory>> entry : createdART.entrySet()) {
                        switch (entry.getKey()) {
                            case ACTION:
                                registerActions(entry.getValue().entrySet().stream().collect(toMap(Map.Entry::getKey, artFactory -> (ActionFactory<?, ?>) artFactory.getValue())));
                                break;
                            case REQUIREMENT:
                                registerRequirements(entry.getValue().entrySet().stream().collect(toMap(Map.Entry::getKey, artFactory -> (RequirementFactory<?, ?>) artFactory.getValue())));
                                // TODO: register other art types
                        }
                    }
                })
                .accept(artBuilder);
    }

    void registerActions(Map<String, ActionFactory<?, ?>> actions) {

        actions().register(actions);
        getLogger().info("  --- " + actions.size() + "x ACTIONS ---");
        actions.keySet().forEach(actionIdentifier -> getLogger().info("     " + actionIdentifier));
    }

    void registerRequirements(Map<String, RequirementFactory<?, ?>> requirements) {
        throw new NotImplementedException();
    }

    @Override
    public ARTResult create(ARTConfig config) {
        try {
            if (!getParser().containsKey(config.getParser())) {
                throw new ARTParseException("Config " + config + " requires an unknown parser of type " + config.getParser());
            }

            return getParser().get(config.getParser()).get().parse(config);
        } catch (ARTParseException e) {
            logger.warning(e.getMessage());
            return new EmptyARTResult();
        }
    }

    @Override
    public ActionManager actions() {
        return actionManager;
    }

    @Override
    public <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
        throw new NotImplementedException();
    }
}
