package net.silthus.art.api;

import com.google.inject.ImplementedBy;
import net.silthus.art.ARTBuilder;
import net.silthus.art.DefaultARTManager;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionManager;
import net.silthus.art.api.config.ARTConfig;
import net.silthus.art.api.parser.ARTResult;
import net.silthus.art.api.requirements.Requirement;
import net.silthus.art.api.trigger.TriggerContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The {@link ARTManager} is the core piece of the ART framework.
 * It manages the registration, creation and orchestration of the corresponding {@link ARTObject}s.
 * <br>
 * Use it to access all relevant methods related to the framework.
 * You can also provide your own implementation by calling {@link net.silthus.art.ART#setInstance(ARTManager)}.
 * <br>
 *     <ul>
 *         <li>Register your {@link ARTObject}s by creating an {@link ARTBuilder} with {@link #register(String, Consumer)}.</li>
 *         <li>Trigger {@link Action}s and {@link Requirement}s with {@link #trigger(String, Object, Predicate)}.</li>
 *         <li></li>
 *     </ul>
 */
@ImplementedBy(DefaultARTManager.class)
public interface ARTManager {

    boolean isLoaded();

    void load();

    void unload();

    void register(String pluginName, Consumer<ARTBuilder> builder);

    ActionManager actions();

    /**
     * Parses the given {@link ARTConfig} and creates {@link ARTObject} instances wrapped as {@link ARTContext}.
     * <br>
     * Use this to create a list of {@link ARTObject}s that you can use inside your plugin, e.g. to check requirements.
     *
     * @param config art config to parse and create {@link ARTResult} from.
     * @return an {@link ARTResult} containing all parsed {@link ARTObject}s.
     * @see ARTResult
     * @see ARTObject
     * @see ARTContext
     */
    ARTResult create(ARTConfig config);

    <TTarget, TConfig> void trigger(String identifier, TTarget target, Predicate<TriggerContext<TTarget, TConfig>> context);
}
