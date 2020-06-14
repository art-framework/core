package net.silthus.art.api.actions;

import com.google.inject.ImplementedBy;
import net.silthus.art.actions.DefaultActionManager;
import net.silthus.art.api.ARTFactory;
import net.silthus.art.api.config.ARTConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ImplementedBy(DefaultActionManager.class)
public interface ActionManager {

    /**
     * Creates a dummy implementation of an {@link ActionManager}.
     * The implementation returns empty but not null values for every method.
     * Use it to mock your tests.
     * <br>
     * The null instance will also be returned if ART was not properly initialized before making the first call.
     *
     * @return empty implementation of the {@link ActionManager}
     */
    static ActionManager nullManager() {
        return new NullActionManager();
    }

    /**
     * Checks if a {@link net.silthus.art.api.ARTFactory} exists for the given identifier.
     *
     * @param identifier identifier of the {@link net.silthus.art.api.ARTObject}
     * @return true if an {@link net.silthus.art.api.ARTFactory} exists, false otherwise
     */
    boolean exists(String identifier);

    /**
     * Registers the given {@link ActionFactory} types with the {@link ActionManager}.
     * This should happen whenever a plugin registers their ART.
     *
     * @param actionFactories factories to register
     */
    void register(Map<String, ActionFactory<?, ?>> actionFactories);

    /**
     * Tries to find a matching {@link net.silthus.art.api.ARTFactory} for the given identifier.
     * Use the factory to crate instances of the {@link net.silthus.art.api.ARTObject} wrapped as {@link net.silthus.art.api.ARTContext}.
     * <br>
     * Returns {@link Optional#empty()} if no {@link net.silthus.art.api.ARTObject} with a matching identifier is found.
     *
     * @param identifier identifier of the {@link net.silthus.art.api.ARTObject}
     * @return matching {@link net.silthus.art.api.ARTFactory} or an empty {@link Optional} if no {@link net.silthus.art.api.ARTObject} with the identifier was found
     * @see net.silthus.art.api.ARTFactory
     */
    Optional<ActionFactory<?, ?>> getFactory(String identifier);

    /**
     * Parses the given {@link ARTConfig} and creates {@link Action} instances wrapped as {@link ActionContext}.
     * <br>
     * Use this to create a list of {@link Action}s that you can use inside your plugin, e.g. to check requirements.
     *
     * @param config art config to parse and create {@link Action}s from.
     * @return a list of {@link Action}s found inside the {@link ARTConfig} wrapped as {@link ActionContext}
     * @see Action
     * @see ARTConfig
     * @see ActionContext
     */
    List<ActionContext<?, ?>> create(ARTConfig config);

    /**
     * Parses the given {@link ARTConfig} and creates {@link Action} instances wrapped as {@link ActionContext}.
     * Filters out any {@link Action} not matching the given target Type TTarget.
     * <br>
     * Use this to create a list of {@link Action}s that you can use inside your plugin, e.g. to check requirements.
     *
     * @param targetClass target type to filter actions for
     * @param config art config to parse and create {@link Action}s from.
     * @param <TTarget> type of the target
     * @return a list of {@link Action}s found inside the {@link ARTConfig} wrapped as {@link ActionContext}
     * @see Action
     * @see ARTConfig
     * @see ActionContext
     */
    <TTarget> List<ActionContext<TTarget, ?>> create(Class<TTarget> targetClass, ARTConfig config);
}
