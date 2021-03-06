You will be able to create simple actions, requirements and trigger after completing this quickstart guide for ART developers.  
If you never used the [art-framework](/introduction) before, I highly recommended you take a look at this guide to get a feeling on where to start.

* [Dependencies](#dependencies)
  * [Gradle](#gradle)
  * [Maven](#maven)
* [Using ART](#using-art)
* [Creating ART](#creating-art)
  * [Your first Action](#your-first-action)
  * [Your first Requirement](#your-first-requirement)
  * [Your first Trigger](#your-first-trigger)

However if you want to go deeper down the rabitt hole, head to the detailed documentation of each ART component.

## Dependencies

You only need to depend on the `io.art-framework.core:api` dependency using the [jitpack repository](https://jitpack.io/).

!> Do not shade the art-framework into your plugin.

### Gradle

[gradle](../gradle.md ':include')

### Maven

[maven](../maven.md ':include')

## Using ART

One powerfull feature of the framework is the reuseability of actions, requirements and trigger accross multiple plugins without knowing the implementation and config of those.

The only thing the art-framework needs is a `List<String>` that can be parsed into an `ArtContext`.  

> [!EXAMPLE]
> You have a plugin that wants to reward players for their loyalty.  
> You decide to use the art-framework to make the rewards configurable without depending on a lot of plugins.  
> This will enable users of your plugin to configure rewards with custom requirements and actions and make use of any plugin that uses the art-framework.

This is an example config of the plugin utilizing the art-framework for the rewards:

```yaml
rewards:
    - '?vault:money <1000'
    - '!vault:money.add 10'
    - '!mypets:pet.add(execute_once:true) pikachu'
    - '!item.add(cooldown:48h) diamond amount:16'
```

Register your plugin as an [art-module](/developer/modules) and use the `@OnEnable` tag on a method to parse your config as an `ArtContext`.

```java
@ArtModule("my-plugin")
public class MyPlugin extends JavaPlugin {

    @OnEnable
    public void enableArt(Scope scope) {
        try {
            ArtContext context = scope.load(config.getStringList("rewards"));
            // store the reference to the rewards in a variable somewhere and then execute them when you are ready
            // this will execute all actions that are configured in the config
            context.execute(player);
        } catch (ParseException e) {
            getLogger().severe("failed to load rewards: " + e.getMessage());
        }
    }
}
```

You also have the option to test requirements with `context.test(...)` and listen for trigger execution with `context.onTrigger(...)`. See the [ArtContext documentation](/developer/art-context) for more details.

> [!TIP]
> Take a look at the [module documentation](/developer/modules) to find out more about the lifecycle of modules and what you can do with them.

## Creating ART

All ART that can be used in configs must be created by others and then be provided through modules or plugins. There are three basic `ArtObject`s:

- **[Actions](/developer/actions)** - are used to execute stuff against a defined target type
- **[Requirements](/developer/requirements)** - check predicates to allow or deny execution of trigger or actions
- **[Trigger](/developer/trigger)** - events that trigger action execution

### Your first Action

[Actions](/developer/actions) are used to execute, well actions, against a defined target type. Actions can be used for:

- Teleporting players
- Giving a player money or items
- Spawning mobs
- Damaging an entity or players
- Starting a quest
- Granting an achievement
- Executing a command
- and many, many more...

To keep things simple we are going to create an action that damages the player.

Start by creating a new class and implement the `Action<TTarget>` interface and annotate your class with `@ART` providing an unique identifier to your action.  
The `Action` interface takes a `TTarget` type argument that should be the broadest possible type your action needs.

!> Make sure your class has a parameterless public constructor, as it will be created on the fly everytime it is used.  
  Or take a look at the advanced [art-registration documentation](/developer/registration) how to register your class if it requires some parameters.

[DamageLivingEntityAction.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/actions/DamageLivingEntityAction.java ':include :fragment=header')

You can then use any member variable in your class as a config option by annotating it with `@ConfigOption`.  
These fields will be injected with the configured values when your action is loaded and before the `execute(...)` method is called.

[DamageLivingEntityAction.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/actions/DamageLivingEntityAction.java ':include :fragment=config')

Then implement the logic of your action using the config variables and target parameter.

[DamageLivingEntityAction.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/actions/DamageLivingEntityAction.java ':include :fragment=action')

<details>
<summary>Full example action (click to expand)</summary>

[DamageLivingEntityAction.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/actions/DamageLivingEntityAction.java ':include :fragment=full-example')

</details>

> [!SUCCESS]
> Thats it! You created your first action :)  
> Everything else will be automatically handled for you by the ART-Framework.

> [!TIP]
> Look at the [registration](registration.md) documentation for advanced scenarios where your action requires additional constructor parameters.

*Of course, if you want to you can do a lot more with actions, but thats for another chapter. See the [action documentation](actions.md) if you are interested.*

### Your first Requirement

Now that you created your first action, it is time to add some checks that can test conditions before actions are executed. Here are some examples for requirements:

* the location of a player
* the health of an entity
* online time
* killed mobs
* items in inventory
* and many, many more...

We are still going to keep it simple and implement a requirement that checks the health of an entity.

The process is pretty much the same as the one with the action, except this time we are implementing the `Requirement<TTarget>` interface.

[HealthRequirement.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/requirements/HealthRequirement.java ':include :fragment=header')

The next part is the config, which will be a bit more complicated and take a regex input. This is just to make the requirement more convenient to use. You will see why in the next code example.

[HealthRequirement.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/requirements/HealthRequirement.java ':include :fragment=config')

Then inside the `test(...)` method we are going to check our requirement logic. Here you will see the `return error(...)` statement that can be used when exceptions or config error occur.

[HealthRequirement.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/requirements/HealthRequirement.java ':include :fragment=error')

After no error in the config exists, we are going to check if the actual requirement succeeds and wrap our `boolean` check inside a `resultOf(...)`. If the underlying boolean is `true` a `success()` will be returned and if it is `false` a `failure()` will be returned.

[HealthRequirement.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/requirements/HealthRequirement.java ':include :fragment=result')

And if we put everything together we get a requirement that looks like this.

<details>
<summary>Full example requirement (click to expand)</summary>

[HealthRequirement.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/requirements/HealthRequirement.java ':include :fragment=full-example')

</details>

> [!SUCCESS]
> You are amazing!  
>
> You just finished your first requirement and learned how to create some advanced checks with the art-framework.  
> Everything else will be automagically handled for you.

> [!TIP]
> Look at the [registration](registration.md) documentation for advanced scenarios where your requirement requires additional constructor parameters.

### Your first Trigger

Now that we know how to creation actions and requirements, we just need one last puzzle piece to but together the full `A R T` acronym. The **trigger**.

Triggers are a bit different than actions and requirements, since they are not called by others, but are the source of those calls. Here are some trigger examples:

* any bukkit event
* claiming of a region
* completion of a quest
* trading of items
* and many, many more...

For triggers to work you need something that triggers the trigger, e.g. a bukkit event. In this example we are going to create some very simple trigger for the `PlayerJoinEvent` and `PlayerQuitEvent`.

As the same with the others, we are going to start by implementing the `Trigger` interface. But this time it won't take a target type parameter. We will define that later when we call the trigger.  
You might have noticed that we don't have any methods that need to be implemented. If you need check some requirements against a target in your trigger, implement the `Requirement` interface.

[PlayerJoinTrigger.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/trigger/PlayerJoinTrigger.java ':include')

[PlayerListener.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/trigger/PlayerListener.java ':include')

As you can see you just need to create a class that implements `Trigger` and is annotated with `@ART`. Then call the trigger from inside a bukkit event with the `scope.trigger(...).with(...targets...).execute()` method.  

> [!NOTE]
> You can pass multiple targets to a trigger, which will be used in the execution chain downstream.  
> In the example above the actual event and the player of the event is passed as a target.  
> This makes it possible to use player and event actions in this trigger chain.  
> See the [target](target.md) and [configuration](/configuration/) documentation for more details on the topic.

To register your trigger simply add the class to the trigger provider and register your bukkit listeners.

```java
@ArtModule("my-plugin")
public class MyPlugin extends JavaPlugin {

    @OnLoad
    public void onLoad(Scope scope) {
        
        playerListener = new PlayerListener(scope);
        Bukkit.getPluginManager().registerEvents(playerListener, this);
    }
}
```

> [!TIP]
> Look at the [registration](registration.md) documentation for advanced scenarios where your trigger requires additional constructor parameters.
