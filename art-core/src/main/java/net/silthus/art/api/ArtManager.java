package net.silthus.art.api;

import com.google.inject.ImplementedBy;
import net.silthus.art.ArtBuilder;
import net.silthus.art.ArtModuleDescription;
import net.silthus.art.DefaultArtManager;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFilter;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.trigger.TriggerContext;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The {@link ArtManager} is the core piece of the ART framework.
 * It manages the registration, creation and orchestration of the corresponding {@link ArtObject}s.
 * <br>
 * Use it to access all relevant methods related to the framework.
 * <br>
 *     <ul>
 *         <li>Register your {@link ArtObject}s by creating an {@link ArtBuilder} with {@link #register(ArtModuleDescription, Consumer)}.</li>
 *         <li>Trigger {@link Action}s and {@link Requirement}s with {@link #trigger(String, Object, Predicate)}.</li>
 *         <li></li>
 *     </ul>
 */
@ImplementedBy(DefaultArtManager.class)
public interface ArtManager {

    boolean isLoaded();

    void load();

    void unload();

    Map<Class<?>, List<ArtResultFilter<?>>> getGlobalFilters();

    void register(ArtModuleDescription moduleDescription, Consumer<ArtBuilder> builder);

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
    ArtResult load(ArtConfig config);

    <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context);

    <TTarget> void addGlobalFilter(Class<TTarget> targetClass, ArtResultFilter<TTarget> filter);
}
