package net.silthus.art.api.actions;

import net.silthus.art.api.config.ARTConfig;

import java.util.List;
import java.util.Optional;

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
     * Parses the given {@link ARTConfig} and creates {@link net.silthus.art.api.ARTObject} instances wrapped as {@link net.silthus.art.api.ARTContext}.
     * <br>
     * Use this to create a list of {@link net.silthus.art.api.ARTObject}s that you can use inside your plugin, e.g. to check requirements.
     *
     * @param config art config to parse and create {@link net.silthus.art.api.ARTObject}s from.
     * @return a list of {@link net.silthus.art.api.ARTObject}s found inside the {@link ARTConfig} wrapped as {@link net.silthus.art.api.ARTContext}
     * @see net.silthus.art.api.ARTObject
     * @see ARTConfig
     * @see net.silthus.art.api.ARTContext
     */
    List<Action<?, ?>> create(ARTConfig config);

    <TTarget> List<Action<TTarget, ?>> create(Class<TTarget> targetClass, ARTConfig config);
}
