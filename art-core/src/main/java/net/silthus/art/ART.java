package net.silthus.art;

import lombok.Getter;
import net.silthus.art.api.ARTManager;
import net.silthus.art.api.ARTRegistrationException;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.trigger.TriggerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public final class ART {

    @Getter
    private static Logger logger = Logger.getLogger("ART");
    private static ARTManager instance;

    private static final Map<String, Consumer<ARTBuilder>> queuedRegistrations = new HashMap<>();

    public static void setInstance(ARTManager artManager) {
        if (getInstance().isPresent() && getInstance().get().isLoaded()) {
            throw new UnsupportedOperationException("Cannot change the ARTManager after loading ART. Make sure to change it before calling ART.load()");
        }
        if (getInstance().isPresent()) {
            getLogger().warning("Overriding already registered ARTManager " + instance.getClass().getCanonicalName() + " with " + artManager.getClass().getCanonicalName());
        }
        instance = artManager;
    }

    public static Optional<ARTManager> getInstance() {
        return Optional.ofNullable(instance);
    }

    public static ActionManager actions() {
        return getInstance().map(ARTManager::actions).orElseGet(ActionManager::nullManager);
    }

    public static void load() {
        if (getInstance().isEmpty()) {
            throw new UnsupportedOperationException("No ARTManger found. Cannot load() ART. Make sure to provide an ARTManager with ART.setARTManager(...) before calling ART.load()");
        }
        getInstance().ifPresent(ARTManager::load);
    }

    public static void register(String pluginName, Consumer<ARTBuilder> builder) throws ARTRegistrationException {

        if (getInstance().isEmpty()) {
            queuedRegistrations.put(pluginName, builder);
        } else {
            getInstance().get().register(pluginName, builder);
        }
    }

    public static <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
        getInstance().ifPresent(artManager -> artManager.trigger(identifier, target, context));
    }
}
