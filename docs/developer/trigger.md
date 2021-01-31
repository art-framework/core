Triggers are the source of action calls (in addition to the [art-context](art-context.md)) and require some sort of trigger, e.g. an (bukkit)-event or command. Here are some examples for triggers:

* any bukkit event
* claiming of a region
* completion of a quest
* trading of items
* execution of a command
* and many, many more...

## Creating Triggers

Triggers use the `Trigger` marker interface and must be annotated with the `@ART` [annotation](annotations.md). They can optionally be an [requirement](requirements.md) and implement the `Requirement<TType>` interface. If doing so the requirement implemented by the trigger will be tested before the trigger is executed. You could also extend your requirement and then implement the `Trigger` interface.

Everything will be automagically registered by the art-framework if your trigger has a public parameterless constructor. If not register it using the [scope](scope.md) in the `onLoad()` [lifecycle](modules.md#onload).

```java
@OnLoad
public void onLoad(Scope scope) {
    scope.register().trigger()
        .add(MyTrigger.class, () -> ...);
}
```

Make sure to set `autoRegister = false` in the `@ART` annotation of the requirement if you are manually registering your trigger.

```java
@ART(
    ...
    autoRegister = false
)
public class MyTrigger implements Trigger {}
```

After it has been registered the trigger must be called when the event covered by the trigger is called. Here is a `PlayerMoveEvent` example.

```java
@EventHandler
public void onPlayerMove(PlayerMoveEvent event) {

    ART.trigger(MyTrigger.class)
        .with(event)
        .with(event.getPlayer())
        .execute();
}
```

As you can see the trigger can be called `with(...)` multiple targets and only the requirements and actions matching the targets type or super type are executed and checked.

> [!TIP]
> Do not forget to register your bukkit listener for the trigger events if using that method to execute the trigger.
