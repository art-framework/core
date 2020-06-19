package net.silthus.art;

import lombok.Getter;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.ArtResult;
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
    private static ArtManager instance;

    private static final Map<ArtModuleDescription, Consumer<ArtBuilder>> queuedRegistrations = new HashMap<>();

    public static void setInstance(ArtManager artManager) {

        if (getInstance().isPresent() && getInstance().get().isLoaded()) {
            throw new UnsupportedOperationException("Cannot change the ARTManager after loading ART. Make sure to change it before calling ART.load()");
        }

        if (getInstance().isPresent()) {
            getLogger().warning("Overriding already registered ARTManager " + instance.getClass().getCanonicalName() + " with " + artManager.getClass().getCanonicalName());
        }

        instance = artManager;
    }

    public static Optional<ArtManager> getInstance() {
        return Optional.ofNullable(instance);
    }

    public static void load() {
        if (getInstance().isEmpty()) {
            throw new UnsupportedOperationException("No ARTManger found. Cannot load() ART. Make sure to provide an ARTManager with ART.setARTManager(...) before calling ART.load()");
        }
        ArtManager artManager = getInstance().get();

        for (Map.Entry<ArtModuleDescription, Consumer<ArtBuilder>> entry : queuedRegistrations.entrySet()) {
            artManager.register(entry.getKey(), entry.getValue());
        }

        queuedRegistrations.clear();
        artManager.load();
    }

    /**
     * Use this method to register all of your ART.
     * <br>
     * You can use {@link ArtManager#register(ArtModuleDescription, Consumer)} interchangeable with this method.
     *
     * @param moduleDescription description of the module registering with ART
     * @param builder builder that will be used to register the ART
     * @see ArtManager#register(ArtModuleDescription, Consumer)
     */
    public static void register(ArtModuleDescription moduleDescription, Consumer<ArtBuilder> builder) {

        if (getInstance().isEmpty()) {
            queuedRegistrations.put(moduleDescription, builder);
        } else {
            getInstance().get().register(moduleDescription, builder);
        }
    }

    /**
     * Use this method to create and load ART from your config.
     * <br>
     * You can then use the {@link ArtResult} to invoke {@link net.silthus.art.api.actions.Action}s by calling
     * {@link ArtResult#execute(Object)} or to check for requirements by calling {@link ArtResult#test(Object)}.
     * <br>
     *
     * @param config config to parse and create ART from
     * @return ARTResult containing the parsed config
     * @see ArtManager#create(ArtConfig)
     */
    public static ArtResult create(ArtConfig config) {

        if (getInstance().isEmpty()) {
            return new EmptyArtResult();
        } else {
            return getInstance().get().create(config);
        }
    }

    /**
     *
     * @param identifier
     * @param target
     * @param context
     * @param <TTarget>
     * @param <TConfig>
     * @see ArtManager#trigger(String, Object, Predicate)
     */
    public static <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context) {
        getInstance().ifPresent(artManager -> artManager.trigger(identifier, target, context));
    }
}
