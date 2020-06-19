package net.silthus.art;

import lombok.Getter;
import net.silthus.art.api.ARTManager;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.parser.ARTResult;
import net.silthus.art.api.trigger.TriggerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public final class ART {

    @Getter
    private static final Logger logger = Logger.getLogger("ART");
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

    public static void load() {
        if (getInstance().isEmpty()) {
            throw new UnsupportedOperationException("No ARTManger found. Cannot load() ART. Make sure to provide an ARTManager with ART.setARTManager(...) before calling ART.load()");
        }
        ARTManager artManager = getInstance().get();

        for (Map.Entry<String, Consumer<ARTBuilder>> entry : queuedRegistrations.entrySet()) {
            artManager.register(entry.getKey(), entry.getValue());
        }

        queuedRegistrations.clear();
        artManager.load();
    }

    /**
     * @see ARTManager#register(String, Consumer)
     */
    public static void register(String pluginName, Consumer<ARTBuilder> builder) {

        if (getInstance().isEmpty()) {
            queuedRegistrations.put(pluginName, builder);
        } else {
            getInstance().get().register(pluginName, builder);
        }
    }

    /**
     * @see ARTManager#create(ARTConfig)
     */
    public static ARTResult create(ARTConfig config) {

        if (getInstance().isEmpty()) {
            return new EmptyARTResult();
        } else {
            return getInstance().get().create(config);
        }
    }

    /**
     * @see ARTManager#trigger(String, Object, Predicate)
     */
    public static <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
        getInstance().ifPresent(artManager -> artManager.trigger(identifier, target, context));
    }
}
