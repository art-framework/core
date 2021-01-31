Requirements are used to check certain conditions before executing [actions](actions.md) or [trigger](trigger.md). Without them the `art-framework` would just be a simple wrapper around commands.

Here are some examples what requirements could check:

* the location of a player
* the health of an entity
* online time
* killed mobs
* items in inventory
* and many, many more...

The following section assumes that you have read the [your first requirement](README.md#your-first-requirement) part in the [developer quickstart guide](README.md).

## Creating Requirements

All requirements must implement the `Requirement<TType>` interface and are annotated with the `@ART` [metadata annotation](annotations.md). If they have a public parameterless constructor they will be automagically registered.

If not you can always manually register them with the scope in the [onLoad](modules.md#onload) phase.

```java
@OnLoad
public void onLoad(Scope scope) {
    scope.register().requirements()
        .add(MyRequirement.class, () -> ...);
}
```

Make sure to set `autoRegister = false` in the `@ART` annotation of the requirement if you are manually registering your requirement.

```java
@ART(
    ...
    autoRegister = false
)
public class MyRequirement implements Requirement<...> {}
```

## The Result

Requirements must return a `Result` that represents the outcome of the requirement check. Returning a `failure()` or `error()` means that any trigger or action that comes after the requirement is not executed.

* `success(String...)` - *The requirement check was successfull. Takes an optional log message.*
* `failure(String...)` - *The requirement check failed and the following trigger and actions are not executed. Takes an optional log message why the requirement check failed.*
* `resultOf(boolean, String...)` - *Returns a success or failure depending on the outcome of the input boolean.*
* `error(Exception, String...)` - *There was an error in the execution of the requirement check. All following actions and trigger are not executed. Takes an exception and/or an error message.*

> [!TIP]
> Use the `debug: true` setting in the config to increase the log level and show all requirement checks and their results.
