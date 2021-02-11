package io.artframework;

/**
 * Resolvers are used to resolve complex config types from primitives and strings.
 * <p>Resolvers are statically analyzed and resolved when an {@link ArtContext} is created
 * from an {@link io.artframework.parser.Parser}. Dynamic values should be passed as targets
 * or provided through the {@link ExecutionContext}.
 * <p>A new instance of a {@code Resolver} is created for every resolution configuration
 * parsed when creating an {@code ArtContext}.
 * <p>Resolvers can be nested but care must be taken to not create an endless loop.
 * <p>For example an {@code PlayerInventoryResolver<PlayerInventory>} can use the
 * {@code PlayerResolver<Player>} to resolve the player argument.
 * <pre>{@code
 * public class PlayerInventoryResolver implements Resolver<PlayerInventory> {
 *
 *      @Resolve
 *      @ConfigOption(required = true)
 *      private Player player;
 *
 *      public PlayerInventory resolve(ResolveContext context) {
 *          return player.getInventory();
 *      }
 * }
 * }</pre>
 * @param <TType> the type that is resolved by this resolver
 */
@FunctionalInterface
public interface Resolver<TType> extends ArtObject {

    /**
     * Resolves the type of this resolver using the provided and injected config values.
     * <p>Return null or throw an {@link ResolveException} if the resolution failed.
     *
     * @param context the context of the resolution
     * @return the resolved type or null if type was not found
     * @throws ResolveException if the resolution of the target type failed
     */
    TType resolve(ResolveContext context) throws ResolveException;
}
