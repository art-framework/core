package net.silthus.art;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.silthus.art.api.ArtFactory;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtParseException;
import net.silthus.art.api.parser.ArtParser;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.requirements.RequirementFactory;
import net.silthus.art.api.trigger.TriggerContext;
import net.silthus.art.util.ConfigUtil;
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
public class DefaultArtManager implements ArtManager {

    private final ActionManager actionManager;

    private Logger logger = Logger.getLogger("ART");
    private final Map<String, Provider<ArtParser>> parser;
    private final Map<ArtModuleDescription, ArtBuilder> registeredPlugins = new HashMap<>();

    @Inject
    public DefaultArtManager(ActionManager actionManager, Map<String, Provider<ArtParser>> parser) {
        this.actionManager = actionManager;
        this.parser = parser;
    }

    @Setter(AccessLevel.PACKAGE)
    private boolean loaded = false;

    @Override
    public void load() {

        setLoaded(true);
        getLogger().info("-------- ART MANAGER LOADED --------");
    }

    @Override
    public void unload() {

        setLoaded(false);
        getLogger().info("-------- ART MANAGER UNLOADED --------");
    }

    @Override
    public void register(ArtModuleDescription moduleDescription, Consumer<ArtBuilder> builder) {

        ArtBuilder artBuilder;
        if (registeredPlugins.containsKey(moduleDescription)) {
            artBuilder = registeredPlugins.get(moduleDescription);
        } else {
            artBuilder = new ArtBuilder();
        }

        builder.andThen(art -> registeredPlugins.put(moduleDescription, art))
                .andThen(art -> {
                    getLogger().info("--------------------------------------------------");
                    getLogger().info("   " + moduleDescription.getName() + " v" + moduleDescription.getVersion() + " registered their ART.");
                    getLogger().info("");

                    Map<Class<?>, Map<String, ArtFactory<?, ?, ?>>> createdART = art.build();
                    for (Map.Entry<Class<?>, Map<String, ArtFactory<?, ?, ?>>> entry : createdART.entrySet()) {
                        if (entry.getKey() == ActionFactory.class) {
                            registerActions(entry.getValue().entrySet().stream().collect(toMap(Map.Entry::getKey, artFactory -> (ActionFactory<?, ?>) artFactory.getValue())));
                        } else if (entry.getKey() == RequirementFactory.class) {
                            registerRequirements(entry.getValue().entrySet().stream().collect(toMap(Map.Entry::getKey, artFactory -> (RequirementFactory<?, ?>) artFactory.getValue())));
                        }
                    }

                    getLogger().info("--------------------------------------------------");
                    getLogger().info("");
                })
                .accept(artBuilder);
    }

    void registerActions(Map<String, ActionFactory<?, ?>> actions) {

        actions().register(actions);
        getLogger().info("   " + actions.size() + "x Action(s):");
        actions.keySet().forEach(actionIdentifier -> getLogger().info("    - " + actionIdentifier));
        getLogger().info("");
    }

    void registerRequirements(Map<String, RequirementFactory<?, ?>> requirements) {
        throw new NotImplementedException();
    }

    @Override
    public ArtResult load(ArtConfig config) {
        try {
            if (!getParser().containsKey(config.getParser())) {
                throw new ArtParseException("Config " + config + " requires an unknown parser of type " + config.getParser());
            }

            return getParser().get(config.getParser()).get().parse(config);
        } catch (ArtParseException e) {
            logger.severe("ERROR in " + ConfigUtil.getFileName(config.getId()).orElse("unknown config") + ":");
            logger.severe("  --> " + e.getMessage());
            return new EmptyArtResult();
        }
    }

    public ActionManager actions() {
        return actionManager;
    }

    @Override
    public <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
        throw new NotImplementedException();
    }
}
