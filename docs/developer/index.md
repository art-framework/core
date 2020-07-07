# Developer Documentation

The [ART-Framework](https://github.com/silthus/art-framework) is designed to be modular and very easy to use for both developers and [server admins](../admin/README.md).

All of these code examples can also be found inside the [art-example](../../art-example/src/main/java/net/silthus/examples/art/) project.

* [Dependencies](#dependencies)
  * [Gradle](#gradle)
  * [Maven](#maven)
* [Creating Actions](#creating-actions)
* [Creating Requirements](#creating-requirements)
* [Creating Trigger](#creating-trigger)
* [Register your **A**ctions **R**equirements **T**rigger](#register-your-actions-requirements-trigger)
* [Using **A**ctions **R**equirements **T**rigger in your plugin](#using-actions-requirements-trigger-in-your-plugin)
  * [Using Bukkit's ConfigurationSection](#using-bukkits-configurationsection)
  * [Using ConfigLib](#using-configlib)

## Dependencies

You only need to depend on the `net.silthus.art:art-core` or the corresponding implementation, e.g. `net.silthus.art:art-bukkit`.

### Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation group: 'net.silthus.art', name: 'art-core', version: '1.0.0-alpha.7'
}
```

### Maven

```xml
<project>
  ...
  <dependencies>
    <dependency>
      <groupId>net.silthus.art</groupId>
      <artifactId>art-core</artifactId>
      <version>1.0.0-alpha.7</version>
      <scope>provided</scrope>
    </dependency>
  </dependencies>
  ...
</project>
```

## Creating Actions

You can provide `actions`, `requirements` and `trigger` from any of your plugins. These will be useable by [Server Admins](../admin/README.md) inside configs used by other plugins.

Providing an `Action` is as simple as implementing the `Action<TTarget, TConfig>` interface and registering it with `ART.register(...)`.

First create your action and define a config (optional). In this example a `PlayerDamageAction` with its own config class.

```java
/**
 * Every action needs a unique name across all plugins.
 * It is recommended to prefix it with your plugin name to make sure it is unique.
 *
 * The @Name annotation is required on all actions or else the registration will fail.
 *
 * You can optionally provide a @Config and a @Description
 * that will be used to describe the parameter your action takes.
 */
@Name("art-example:player.damage")
@Description("Optional description of what your action does.")
@Config(PlayerDamageAction.ActionConfig.class)
public class PlayerDamageAction implements Action<Player, PlayerDamageAction.ActionConfig> {

    /**
     * This method will be called everytime your action is executed.
     *
     * @param target the player or other target object your action is executed against
     * @param context context of this action.
     *                Use the {@link ActionContext} to retrieve the config
     */
    @Override
    public void execute(Target<Player> target, ActionContext<Player, ActionConfig> context) {
        context.getConfig().ifPresent(config -> {
            // the target object is always wrapped in a Target<?> class
            // this makes it easy to provide a consistent unique id across different targets
            // use the target.getUniqueId() method if you want to cache something related to the given target instance
            Player player = target.getSource();
            double damage;
            double health = player.getHealth();
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            if (config.percentage) {
                if (config.fromCurrent) {
                    damage = health * config.amount;
                } else {
                    damage = maxHealth * config.amount;
                }
            } else {
                damage = config.amount;
            }

            player.damage(damage);
        });
    }

    /**
     * You should annotate all of your config parameters with a @Description.
     * This will make it easier for the admins to decide what to configure.
     *
     * You can also tag config fields with a @Required flag.
     * The action caller will get an error if the parameter is not defined inside the config.
     *
     * Additionally to that you have to option to mark your parameters with the @Position position.
     * Start in an indexed manner at 0 and count upwards. This is optional.
     *
     * This means your action can be called like this: !art-example:player.damage 10
     * instead of: !art-example:player.damage amount=10
     */
    public static class ActionConfig {

        // the config class needs to have a parameterless public contructor
        // and needs to be static if it is an inner class

        @Required
        @Position(0)
        @Description("Damage amount in percent or health points. Use a value between 0 and 1 if percentage=true.")
        private double amount;

        @Description("Set to true if you want the player to be damaged based on his maximum life")
        private final boolean percentage = false;

        @Description("Set to true if you want to damage the player based on his current health. Only makes sense in combination with percentage=true.")
        private final boolean fromCurrent = false;
    }
}
```

## Creating Requirements

Requirements work just the same as [Actions](#creating-actions), except that they are there to test conditions. They can then be used by [admins](../admin/README.md) to test conditions before executing actions or reacting to triggers.

Simply implement the `Requirement<TTarget, TConfig>` interface and register in with `ART.register(...)`.

> Return `true` if the check was successfull, meaning actions can be executed.  
> And return `false` if any check failed and nothing should be executed.

See the comments on the [action-example](#creating-actions) for details on the annotations.

```java
@Name("art-example:location")
@Config(LocationConfig.class)
@Description({
        "Checks the position of the entity.",
        "x, y, z, pitch and yaw are ignored if set to 0 unless zeros=true.",
        "Check will always pass if no config is set.",
        "For example: '?art-example:location y:256' will be true if the player reached the maximum map height."
})
public class EntityLocationRequirement implements Requirement<Entity, LocationConfig> {

    @Override
    public boolean test(Target<Entity> entity, RequirementContext<Entity, LocationConfig> context) {

        if (!context.getConfig().isPresent()) return true;

        return context.getConfig()
                .map(locationConfig -> locationConfig.isWithinRadius(entity.getSource().getLocation()))
                .orElse(true);
    }
}

// you can create reusable config classes
// this config is used in the requirement and trigger (see below)
@Data
public class LocationConfig {

    @Position(0)
    int x;
    @Position(1)
    int y;
    @Position(2)
    int z;
    @Position(3)
    String world;
    @Position(4)
    int radius;
    float yaw;
    float pitch;
    @Description("Set to true to check x, y, z, pitch and yaw coordinates that have a value of 0.")
    boolean zeros = false;

    /**
     * Maps this config to the given location.
     * Replacing all default values with the location values.
     *
     * @param location location to replace default values with
     * @return new location with combined values from the config and the given location
     */
    public Location toLocation(Location location) {
        Location newLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
        if (!Strings.isNullOrEmpty(world)) {
            World world = Bukkit.getWorld(this.world);
            if (world != null) newLocation.setWorld(world);
        }
        if (isApplied(x)) newLocation.setX(x);
        if (isApplied(y)) newLocation.setY(y);
        if (isApplied(z)) newLocation.setZ(z);
        if (isApplied(pitch)) newLocation.setPitch(pitch);
        if (isApplied(yaw)) newLocation.setYaw(yaw);

        return newLocation;
    }

    /**
     * Checks if the given location is within the radius of the location configured by this config.
     *
     * @param location location to check config against
     * @return true if the location is within this configs radius
     */
    public boolean isWithinRadius(Location location) {
        return LocationUtil.isWithinRadius(toLocation(location), location, radius);
    }

    private boolean isApplied(Number value) {
        return (value.floatValue() != 0 || value.intValue() != 0) || zeros;
    }
}
```

## Creating Trigger

Trigger are a little different than [actions](#creating-actions) and [requirements](#creating-requirements). You can define multiple Trigger in one and the same class and can even fire trigger from anywhere in your plugin. The only requirement is that all triggers that are fired must be [registered](#register-your-actions-requirements-trigger) before they are executed.

Implement the `Trigger` interface to mark the presence of triggers in your class. Then annotate every method that fires a trigger with `@Name`, `@Description` and optionally a `@Config` parameter.

```java
public class PlayerMoveTrigger implements Trigger, Listener {

    private static final String PLAYER_MOVE = "art-example:player.move";

    // the name of the trigger must be unique
    // and the match the identifier used in the trigger(...) method
    @Name(PLAYER_MOVE)
    // it is considered best practice to provide a good description for your trigger
    @Description({
            "Triggers if the player moved to the given location.",
            "Will only check full block moves and not every rotation of the player."
    })
    // annotate your trigger the config it uses
    // here we reuse the same config we used in the requirement
    @Config(LocationConfig.class)
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        // we want to make sure the player actually moved a block
        // otherwise the trigger would fire on every camera movement of the player
        if (!hasMoved(event)) return;

        // this is the actual method to fire the trigger
        trigger(PLAYER_MOVE, test(event.getTo()), event.getPlayer());
    }

    // here you can see an example on how to make your code more structured
    // you could also reuse this check for requirements and other triggers
    // by placing it inside a static utility class
    private Predicate<TriggerContext<LocationConfig>> test(Location location) {
        return context -> {
            if (location == null) {
                return true;
            }
            return context.getConfig()
                    .map(locationConfig -> locationConfig.isWithinRadius(location))
                    .orElse(true);
        };
    }

    private boolean hasMoved(PlayerMoveEvent event) {
        if (event.getTo() == null) return false;

        return event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }
}
```

> You can create triggers for multiple events in the same class.  
> Just make sure that every method that fires a trigger has the required annotations.

## Register your **A**ctions **R**equirements **T**rigger

You need to register your actions, requirements and trigger when your plugin is enabled. Before you can do that, you need to make sure ART is loaded and enabled.

You can use the static `ART` class to register your actions, requirements and trigger. However you need to make sure ART is loaded before calling it, to avoid `ClassNotFoundExceptions`.

```java
public class ExampleARTPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        // register your actions, requirements and trigger when enabling your plugin
        registerART();
    }

    private void registerART() {

        if (!isARTLoaded()) {
            getLogger().warning("ART plugin not found. Not registering ART.");
            return;
        }

        ART.register(ArtBukkitDescription.ofPlugin(this), artBuilder -> artBuilder
                .target(Player.class)
                    .action(new PlayerDamageAction())
                    .and()
                    .trigger(new PlayerMoveTrigger())
                .and(Entity.class)
                    .requirement(new EntityLocationRequirement()));
    }

    private boolean isARTLoaded() {
        return Bukkit.getPluginManager().getPlugin("ART") != null;
    }
}
```

## Using **A**ctions **R**equirements **T**rigger in your plugin

One powerfull feature ob the [ART-Framework](https://github.com/silthus/art-framework) is the reuseability of actions, requirements and trigger accross multiple plugins without knowing the implementation and config of those.

All you need to do to use ART inside your plugin is to provide a reference to the loaded `ARTConfig`. How you load this config is up to you.  
However to make your life simple ART provides some helper methods for Bukkits `ConfigurationSection` and implements [ConfigLib](https://github.com/Silthus/ConfigLib) for easy configuration loading.

> Make sure you load your ARTConfig after all plugins are loaded and enabled.  
> To do this you can use this handy method: `Bukkit.getScheduler().runTaskLater(this, () -> {...}, 1L);`  
> This will execute after all plugins are loaded and enabled.

The following example references an `example.yml` config which could have this content. For more details see the [admin documentation](../admin/README.md).

```yaml
actions:
  art:
    - '?art-example:location y:256 radius:5'
    # kill the player if he is 5 blocks away from the top of the map
    - '!art-example:player.damage 1.0 percentage:true'
    - '!text "You reached the heavens of the gods and will be punished!"'
```

You need an `ArtResult` to execute actions, test for requirements or listen to triggers. You can find two methods below on how to create such an `ArtResult`.

```java
// here we asume that you have loaded your ArtResult (see below)
ArtResult result = ART.load(config);

// you can execute all actions that are in the ArtResult
result.execute(player);

// or test if all requirements are met
if (result.test(player)) {
    // do stuff
}

// or listen if any of the trigger gets executed
getArtResult().onTrigger(Player.class, target -> {
    Player player = target.getSource();
    player.damage(20);
});
```

### Using Bukkit's ConfigurationSection

You need to depend on the `net.silthus.art:art-bukkit` module if you want to load your configurations using a Bukkit `ConfigurationSection`.  
Then you can simply do the following when your plugin gets enabled.

```java
public class ExampleARTPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        // this will load all art configs after all plugins are loaded and enabled
        // this is a must to avoid loading conflicts
        Bukkit.getScheduler().runTaskLater(this, this::loadARTConfig, 1L);
    }

    private void loadARTConfig() {

        if (!isARTLoaded()) {
            getLogger().warning("ART plugin not found. Not loading ART configs.");
            return;
        }

        File configFile = new File(getDataFolder(), "example.yml");
        // copy the example.yml config from our jar file (resources folder) to the plugin directory
        // this is completly optional and you can also load your files some other way
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("example.yml", false);
        }

        try {
            // provide the file to the config and your key where the art section should begin
            ArtConfig config = BukkitArtConfig.of(configFile, "actions");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        // use the ARtResult to execute, test or listen to the ART
        ARTResult artResult = ART.load(config);
    }

    private boolean isARTLoaded() {
        return Bukkit.getPluginManager().getPlugin("ART") != null;
    }
}
```

### Using [ConfigLib](https://github.com/Silthus/ConfigLib)

[ConfigLib](https://github.com/Silthus/ConfigLib) allows you to load your configurations into predefined classes and gets rid of all the magic strings Bukkit uses.  
You should give it a try, it is just awesome :)

```java
public class ExampleARTPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        // this will load all art configs after all plugins are loaded and enabled
        // this is a must to avoid loading conflicts
        Bukkit.getScheduler().runTaskLater(this, this::loadARTConfig, 1L);
    }

    private void loadARTConfig() {

        if (!isARTLoaded()) {
            getLogger().warning("ART plugin not found. Not loading ART configs.");
            return;
        }

        // this will load the config using ConfigLib
        // see https://github.com/Silthus/ConfigLib/ for more details
        Config config = new Config(new File(getDataFolder(), "example.yml"));
        config.loadAndSave();

        // use the ARtResult to execute, test or listen to the ART
        ARTResult artResult = ART.load(config);
    }

    private boolean isARTLoaded() {
        return Bukkit.getPluginManager().getPlugin("ART") != null;
    }

    // ConfigLib allows you to use statically typed configs
    // without the hassle of guessing property names
    @Getter
    @Setter
    public static class Config extends YamlConfiguration {

        private final ARTConfig actions = new ARTConfig();

        protected Config(File file) {
            super(file.toPath());
        }
    }
}
```
