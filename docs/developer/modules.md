# Modules

Modules are the heart of the art-framework and every addition or modification of the framework comes from a module.

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

## Creating Modules

Any class can be a module. You just need to tag the class with the `@ArtModule` annotation and provide a unique identifier for your module.

```java
@ArtModule("my-module")
public class MyModule {}
```

> [!NOTE]
> Your module must be inside the `jar` file of your bukkit plugin or placed into the `modules/` directory of the art-framework.

## Configurations

All fields that are annotated with `@Config(String)` will be injected the given object as a configuration parsed from the provided file name.

The injection of the configuration happens before any lifecycle method is called.

> [!NOTE]
> The config is located inside the `modules/your-module/` directory and will be created if it does not exist.

```java
@ArtModule("foobar")
public class MyModule {

    // this will try to map the my-config.yml file to the fields inside MyConfig
    // and inject it into this field when the module is initialized and before bootstrap is called
    @Config("my-config.yml")
    private MyConfig config;
}
```

## The Module Lifecycle

Every art-module goes through the same lifecycle, which you can hook into with various method tags. Every method, except the bootstrap method takes [Scope](/developer/scope) object.

A module does not require any lifecycle method to work. All methods work completly independant from each other and should only be used if required.

!> The lifecycle methods of dependencies will always be called before the lifecycle method of the dependent module.

### OnBootstrap

The `onBootstrap(BootstrapScope)` method is the first to be called before building the configuration object with all of its providers.
It is the only method that takes a different argument, the `BootstrapScope`.

Use the bootstrap lifecycle to overwrite or register your own customer providers, e.g. a different scheduler or storage provider.

```java
@OnBootstrap
public void onBootstrap(BootstrapScope scope) {
    scope.configure(config -> config.scheduler(new MyCustomScheduler()));
}
```

### OnLoad

The load phase comes directly after all modules finished the bootstrap phase. The configuration is fixed and cannot be changed at this point.

Use the `load(Scope)` method annotated with `@OnLoad` to load your configuration (which was injected before bootstrapping) or to do other stuff that prepares your module.
You **should not** use the load method to start any logic of your module. Use the enable lifecycle for that.

> One very common use case of the `@OnLoad` hook is the registration of your art-objects.

```java
private enableMyModule = false;

@OnLoad
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

```java
@Config("config.yml")
private Config config;

@OnEnable
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
@OnDisable
public void onDisable(Scope scope) {
    // store some persistent data for this module under the key: foo
    scope.store().set(this, "foo", myData);
}
```

### OnReload

The reload method is called everytime your module is reloaded and the a configuration reload was requested.
Before the call all configuration references tagged with `@Config` will be reloaded and a fresh instance injected.

```java
@OnReload
public void onReload(Scope scope) {

}
```
