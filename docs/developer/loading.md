Loading or parsing a [configuration](/configuration) or list of strings into an [ArtContext](art-context.md) is the central function of the art-framework. It allows you to take an input from your users and apply the powerfull art-framework engine to it.

> [!EXAMPLE]
> The [RCAchievements](https://github.com/raidcraft/rcachievements/) plugin uses all of the following methods to [load trigger](https://github.com/raidcraft/rcachievements/blob/master/src/main/java/de/raidcraft/achievements/types/ArtAchievement.java#L49), [check requirements](https://github.com/raidcraft/rcachievements/blob/master/src/main/java/de/raidcraft/achievements/types/ArtAchievement.java#L52) and [execute actions](https://github.com/raidcraft/rcachievements/blob/master/src/main/java/de/raidcraft/achievements/listener/RewardListener.java#L55) to give player rewards when completing an achievement.

<!-- panels:start -->
<!-- div:title-panel -->
## Executing Actions

The **first use-case** of the art-framework we are going to look at is **executing actions**.

> [!USECASE]
> Do you have a plugin that executes commands because you need to do something after an event in your plugin occured, e.g. giving player rewards?  
> Using the art-framework gives your users a lot more flexibility, defining cooldowns, requirements and more.

<!-- div:left-panel -->
The following code snippet parses a list of strings from your configuration using the [global scope](scope.md) and [flow parser](parser.md) and then executes all actions that are configured in the parsed string list.

```java
try {
    // parses the given list of strings into an ArtContext
    ArtContext actions = ART.load(config.getStringList("rewards"));
    // store the context and execute it when you want to give a player the rewards
    actions.execute(player);
} catch (ParseException e) {
    // an exception is thrown if an art-object does not exist or a parsing error occured
    getLogger().warning("an error occured while parsing the rewards: " + e.getMessage());
}
```
<!-- div:right-panel -->
Allowing your users to define art-rewards makes your plugin much more flexible compared to the standard execute a command. Take a look at the [action configuration](../configuration/actions.md) documentation to learn more about the available options.

```yaml
rewards:
  # checks if the minecraft level of the player is above 5
  - '?level >5'
  # adds 1000 money to the player with a cooldown of 1h
  - '!money.add(cooldown:1h) 1000'
  - '?level >10'
  # only spawns the mythtic mob once per player
  - '!mythicmobs:mob.spawn(execute_once:true) endboss'
```
<!-- div:title-panel -->
## Checking Requirements

The **second use-case** of the art-framework is to **check requirements** before doing an internal action in your plugin.

> [!USECASE]
> You have a command that needs to check requirements across multiple plugins before executing it.  
> Using the art-framework allows you to do that without depending on the plugins and allowing your users to define what to check.

<!-- div:left-panel -->
The parsing process is the same everytime. You decide what you expect from the parsed result and act on the `ArtContext` accordingly.

```java
try {
    // parses the given list of strings into an ArtContext
    ArtContext requirements = ART.load(config.getStringList("checks"));
    // store the context and then check the requirements when needed
    Result result = requirements.test(player);
    if (result.success()) {
        // the requirement check was successful
    } else {
        // an error occured or the check failed
    }
} catch (ParseException e) {
    // an exception is thrown if an art-object does not exist or a parsing error occured
    getLogger().warning("an error occured while parsing the art: " + e.getMessage());
}
```
<!-- div:right-panel -->
Multiple requirements are combined using an `AND` statement. Take a look at the [requirement configuration](../configuration/requirements.md) for more details.

```yaml
checks:
  # checks if the minecraft level of the player is above 5
  # AND
  # if the player is at the given coordinates in the nether
  - '?level >5'
  - '?loc 1337,64,-1337 radius:10 world:world_nether'
```
<!-- div:title-panel -->
## Listening for Trigger

The **third** and last **use-case** is listening on [trigger](trigger.md) events and then act on that in your plugin.

> [!USECASE]
> You have a plugin that gives players achievements when they execute an action.  
> Using the art-framework you can give your users very powerfull configuration options for configuring the achievements.

<!-- div:left-panel -->
The following code gets execute when the configured trigger is called.

!> Make sure to call `enableTrigger()` on the `ArtContext` or else the trigger listener will not be enabled.

```java
try {
    // parses the given list of strings into an ArtContext
    ArtContext trigger = ART.load(config.getStringList("trigger"));
    trigger.enableTrigger().onTrigger(Player.class, (target, context) -> {
        Player player = target.source();
        // do something with the player reacting to the trigger
    });
} catch (ParseException e) {
    // an exception is thrown if an art-object does not exist or a parsing error occured
    getLogger().warning("an error occured while parsing the art: " + e.getMessage());
}
```
<!-- div:right-panel -->
Trigger can have requirements and execute actions if they are successfull. Take a look at the [trigger documentation](../configuration/trigger.md) for more details on the configuration.

```yaml
checks:
  # the trigger only gets executed when the player reaches
  # the coordinates with a stick in his hand
  - '?equipment item:stick slot:hand'
  - '@location 0,64,0'
  # the action is executed additionally to the trigger listener of the code above
  - '!txt "You reached the spawn with a stick in your hand!"'
```
<!-- panels:end -->