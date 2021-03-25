package io.artframework;

import io.artframework.annotations.*;

public interface Module {
    /**
     * The bootstrap method is called once on all modules before any module is loaded or enabled.
     * You can configure the {@link BootstrapScope} provide your own provider implementations.
     * <p>
     * Loading modules that require bootstrapping after the bootstrap stage is finished will fail.
     * Removing bootstrap modules and then reloading the art-framework will fail also. A complete restart is needed.
     * <p>
     * Make sure you only use this method if you really need it and are configuring parts of the art-framework.
     * If you do not use this method your module will be hot pluggable and can be loaded and unloaded on the fly without a restart.
     * <p>
     * The bootstrap lifecycle method is called exactly once in the lifecycle of the module.
     * <p>
     * Any dependencies of this module will be bootstrapped before this module.
     * The lifecycle methods of this module will never be called if this module has missing dependencies.
     * <p>
     * The class must be annotated with the @{@link ArtModule} annotation for the method to be called.
     *
     * @param scope the bootstrap scope that is loading this module
     */
    @OnBootstrap
    default void onBootstrap(BootstrapScope scope) throws Exception {}

    /**
     * The module is loaded after bootstrapping has finished and all configurations have been loaded and injected.
     * Use it to read values from your configuration, register your ART and setup your module.
     * <p>
     * Do not use it to start background jobs, open database connections or anything else that should
     * be running when your module is enabled.
     * Use the {@link OnEnable} lifecycle method for that.
     * <p>
     * The load lifecycle method is called exactly once in the lifecycle of the module.
     * Any reloading will happen with the @{@link OnReload} method.
     * <p>
     * Any dependencies of this module will be loaded before this module.
     * The lifecycle methods of this module will never be called if this module has missing dependencies.
     *
     * @param scope the scope that is loading this module
     */
    @OnLoad
    default void onLoad(Scope scope) throws Exception {}

    /**
     * The method will be called everytime an reload of the art-framework is requested.
     * Use it to reload your configurations and services. You should also clear any cached data to avoid memory leaks.
     * <p>
     * The reload lifecycle method may be called multiple times during the lifecycle of a module.
     *
     * @param scope the scope that is calling the reload on the module
     */
    @OnReload
    default void onReload(Scope scope) throws Exception {}

    /**
     * The enable method is called after bootstrapping has finished and the @{@link OnLoad} method was called.
     * Use it to do the core tasks of your module, e.g. open a database connection, start services, and so on.
     * <p>
     * The enable lifecycle method is called exactly once in the lifecycle of the module.
     * Any reloading will happen with the @{@link OnReload} method.
     * <p>
     * Any dependencies of this module will be enabled before this module.
     * The lifecycle methods of this module will never be called if this module has missing dependencies.
     *
     * @param scope the scope that is enabling the module
     */
    @OnEnable
    default void onEnable(Scope scope) throws Exception {}

    /**
     * The disable method is called when your module was removed from the art-framework and gets disabled.
     * Use it to cleanup any connections, cached data, and so on to prevent memory leaks.
     * <p>
     * The disable lifecycle method is called exactly once in the lifecycle of the module.
     * Any reloading will happen with the @{@link OnReload} method.
     * <p>
     * Any modules that depend on this module will be disabled before disabling this module.
     *
     * @param scope the scope that is disabling the module
     */
    @OnDisable
    default void onDisable(Scope scope) throws Exception {}
}
