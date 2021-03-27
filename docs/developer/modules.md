Modules are the heart of the art-framework and every addition or modification of the framework comes from a module. They can be used to automatically register all of your art-objects that are in the same package as the module. This saves you a lot of typing and nicely packages all of your art in one place.

* [Creating Modules](#creating-modules)
* [Configurations](#configurations)
* [The Module Lifecycle](#the-module-lifecycle)
  * [OnBootstrap](#onbootstrap)
  * [OnLoad](#onload)
  * [OnEnable](#onenable)
  * [OnDisable](#ondisable)
  * [OnReload](#onreload)

**Modules**...

* provide art-objects, like [actions](/developer/actions), [requirements](/developer/requirements) and [trigger](/developer/trigger)
* can replace providers with their own implementation
* extend the art-framework with additional functionality
* are be used to parse an [art-configuration](/configuration) into its [art-context](/developer/art-context)
* are runtime independent - *your plugin will work without shading if the art-framework is not present*

> [!TIP]
> Use the [art-module-template](https://github.com/art-framework/art-module-template) to quickstart your module development. Simply click on "**use template**" and you are good to go.

## Creating Modules

Any class can be a module and just needs to implement `Module` and must be tagged with the `@ArtModule` annotation to provide a unique identifier for your module.

```java
@ArtModule("my-module")
public class MyModule implements Module {}
```

Now all that is left is to register the module with the art-framework when your plugin gets enabled. This will also automatically register all [actions](actions.md), [requirements](requirements.md) and [trigger](trigger.md) that are in the same or a sub package of your modules package. Finetune this behaviour with the [module options](annotations.md#@artmodule).

```java
public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        try {
            ART.register(new MyModule());
        } catch (ClassNotFoundException ex) {
            // the art-framework is not present or not enabled
        }
    }
}
```

> [!NOTE]
> Modules that are loaded from the `modules/` directory of the art-framework are automatically registered.

## Configurations

All fields that are annotated with `@Config(String)` will be injected the given object as a configuration parsed from the provided file name.

The injection of the configuration happens before any lifecycle method is called.

> [!NOTE]
> The config is located inside the `modules/your-module/` directory and will be created if it does not exist.

```java
@ArtModule("foobar")
public class MyModule implements Module {

    // this will try to map the my-config.yml file to the fields inside MyConfig
    // and inject it into this field when the module is initialized and before bootstrap is called
    @Config("my-config.yml")
    private MyConfig config;
}
```

## The Module Lifecycle

Every art-module goes through the same lifecycle, which you can hook into by overriding the various methods. Every method, except the bootstrap method provides the [Scope](/developer/scope) object.

A module does not require any lifecycle method to work. All methods work completly independant from each other and should only be used if required.

!> The lifecycle methods of dependencies will always be called before the lifecycle method of the dependent module.

### OnBootstrap

The `onBootstrap(BootstrapScope)` method is the first to be called before building the configuration object with all of its providers.
It is the only method that takes a different argument, the `BootstrapScope`.

> [!NOTE]
> In the [art-bukkit](/platforms/bukkit) platform the bootstrap phase is called in the `onLoad()` method of the plugin.

Use the bootstrap lifecycle to overwrite or register your own customer providers, e.g. a different scheduler or storage provider.

```java
@Override
public void onBootstrap(BootstrapScope scope) {
    scope.configure(config -> config.scheduler(new MyCustomScheduler()));
}
```

### OnLoad

The load phase comes directly after all modules finished the bootstrap phase. The configuration is fixed and cannot be changed at this point.

> [!NOTE]
> In the [art-bukkit](/platforms/bukkit) platform the load phase is called in the `onLoad()` method of the plugin directly after the bootstrapping was successful.

Use the `load(Scope)` method annotated to load your configuration (which was injected before bootstrapping) or to do other stuff that prepares your module.
You **should not** use the load method to start any logic of your module. Use the enable lifecycle for that.

> One very common use case of the `load(Scope)` hook is the registration of your art-objects.

```java
@Override
public void onLoad(Scope scope) {
    
    // use the load lifecycle hook to register your art-objects
    scope.register()
        .actions()
            .add(...)
        .requirements()
            .add(...)
        .trigger()
            .add();
}
```

### OnEnable

The enable phase is the final phase in the module startup lifecycle. Use it to start the main logic of your module, [parse art-configurations](/developer/parser) and more.

> [!NOTE]
> In the [art-bukkit](/platforms/bukkit) platform the enable phase is called in the `onEnable()` method of the plugin.

```java
@Override
public void onEnable(Scope scope) {
    try {
        ArtContext art = scope.load(config.getStringList("actions"));
    } catch (ParseException e) {

    }
}
```

### OnDisable

The disable lifecycle is called when the art-framework shutsdown are a dependency of your module is disabled.
Cleanup your cache and save non-persistent data for the next startup of your module.

```java
@Override
public void onDisable(Scope scope) {
    // store some persistent data for this module under the key: foo
    scope.store().set(this, "foo", myData);
}
```

### OnReload

The reload method is called everytime your module is reloaded and the a configuration reload was requested.
Before the call all configuration references tagged with `@Config` will be reloaded and a fresh instance injected.

> [!TIP]
> Look at the [config-documentation](config.md) for more details.

```java
@Config("config.yml")
private MyConfig config;

@Override
public void onReload(Scope scope) {

    // config.yml has been reloaded and a fresh instance injected into the config field
}
```
