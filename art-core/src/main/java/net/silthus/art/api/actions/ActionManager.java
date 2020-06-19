package net.silthus.art.api.actions;

import com.google.inject.ImplementedBy;
import net.silthus.art.actions.DefaultActionManager;

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
}
