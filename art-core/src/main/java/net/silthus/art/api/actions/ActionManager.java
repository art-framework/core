package net.silthus.art.api.actions;

import com.google.inject.ImplementedBy;
import net.silthus.art.actions.DefaultActionManager;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.ArtFactory;
import net.silthus.art.api.ArtObject;

import java.util.Map;
import java.util.Optional;

@ImplementedBy(DefaultActionManager.class)
public interface ActionManager {

    /**
     * Checks if a {@link ArtFactory} exists for the given identifier.
     *
     * @param identifier identifier of the {@link ArtObject}
     * @return true if an {@link ArtFactory} exists, false otherwise
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
     * Tries to find a matching {@link ArtFactory} for the given identifier.
     * Use the factory to crate instances of the {@link ArtObject} wrapped as {@link ArtContext}.
     * <br>
     * Returns {@link Optional#empty()} if no {@link ArtObject} with a matching identifier is found.
     *
     * @param identifier identifier of the {@link ArtObject}
     * @return matching {@link ArtFactory} or an empty {@link Optional} if no {@link ArtObject} with the identifier was found
     * @see ArtFactory
     */
    Optional<ActionFactory<?, ?>> getFactory(String identifier);
}
