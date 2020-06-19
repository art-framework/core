package net.silthus.art.api;

import com.google.inject.ImplementedBy;
import net.silthus.art.ArtBuilder;
import net.silthus.art.DefaultArtManager;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.trigger.TriggerContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The {@link ArtManager} is the core piece of the ART framework.
 * It manages the registration, creation and orchestration of the corresponding {@link ArtObject}s.
 * <br>
 * Use it to access all relevant methods related to the framework.
 * You can also provide your own implementation by calling {@link net.silthus.art.ART#setInstance(ArtManager)}.
 * <br>
 *     <ul>
 *         <li>Register your {@link ArtObject}s by creating an {@link ArtBuilder} with {@link #register(String, Consumer)}.</li>
 *         <li>Trigger {@link Action}s and {@link Requirement}s with {@link #trigger(String, Object, Predicate)}.</li>
 *         <li></li>
 *     </ul>
 */
@ImplementedBy(DefaultArtManager.class)
public interface ArtManager {

    boolean isLoaded();

    void load();

    void unload();

    void register(String pluginName, Consumer<ArtBuilder> builder);

    ActionManager actions();

    /**
     * Parses the given {@link ArtConfig} and creates {@link ArtObject} instances wrapped as {@link ArtContext}.
     * <br>
     * Use this to create a list of {@link ArtObject}s that you can use inside your plugin, e.g. to check requirements.
     *
     * @param config art config to parse and create {@link ArtResult} from.
     * @return an {@link ArtResult} containing all parsed {@link ArtObject}s.
     * @see ArtResult
     * @see ArtObject
     * @see ArtContext
     */
    ArtResult create(ArtConfig config);

    <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context);
}
