# Actions

Actions are used to execute actions against a set of [targets](targets.md) fired by a [trigger](trigger.md) after checking [requirements](requirements.md). They can be best compared with commands, but offer far more flexibility and options.

This guide here assumes you have read the [your first action](README.md#your-first-action) part in the [developer quickstart](README.md).

* [Creating Actions](#creating-actions)
* [Generic Actions](#generic-actions)
* [The Result](#the-result)

## Creating Actions

All actions must implement the `Action<TType>` interface and are annotated with the `@ART` [metadata annotation](annotations.md). They also need to be registered with the art-framework in the [onLoad](modules.md#onload) phase.

```java
@OnLoad
public void onLoad(Scope scope) {
    scope.register().actions()
        .add(MyAction.class);
}
```

## Generic Actions

Generic actions, or better known as actions that do not target a specific type, can be registered using lambda expressions and do not require a complete class and `@ART` annotation. If you still want the complete class and specifiy more details in the meta data you can extend the `GenericAction` interface, which is nothing less than an interface that `extends Action<Object>`.

```java
scope.register().actions().add("my-generic-action", (target, context) -> {
    // the target is of type object and the action will be called for every target type
    return success();
});
```

You can also register a lambda action using a specific target type:

```java
scope.register().actions().add("player.kill", Player.class, (target, context) -> {
    // the target type of the action is a player
    Player player = target.source();
    player.setHealth(0);

    return success();
});
```

The downside to these lambda actions is that you can't provide any config options. A usage example would a player kill action that takes no config options (see above).

## The Result

Actions and [Requirements](requirements.md) must return a `Result` that represents the outcome of the action and controls the execution of other actions that come after this one.

* `success(String...)` - *The action was successfully executed. Takes an optional log message.*
* `failure(String...)` - *The execution of the action failed, but all next actions are executed. Takes an optional log message why the action failed.*
* `error(Exception, String...)` - *There was an error in the exection of the action. All following actions are not executed. Takes an exception and/or an error message.*

> [!TIP]
> Use the `debug: true` setting in the config to increase the log level and show all action execution and results.
