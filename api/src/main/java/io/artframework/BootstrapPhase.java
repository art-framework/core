package io.artframework;

/**
 * The bootstrap phase is a special state of the module provider
 * after bootstrapping all modules has finished and before they are loaded.
 * <p>Use the {@link #loadAll()} and {@link #enableAll()} methods to load and enable
 * all modules in the configuration after the configuration is complete and sealed.
 */
public interface BootstrapPhase extends BootstrapScope {

    /**
     * Loads all modules in the scope.
     *
     * @return the current bootstrap phase
     */
    BootstrapPhase loadAll();

    /**
     * Enables all modules in the scope.
     * <p>Will also load all modules if they have not been loaded.
     *
     * @return the scope of the finished bootstrap phase
     */
    Scope enableAll();
}
