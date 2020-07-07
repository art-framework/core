# Server Admin Documentation

* [Getting started](#getting-started)
* [ART Config Syntax](#art-config-syntax)
  * [ART Types](#art-types)
  * [Basic Example](#basic-example)
  * [Multiple Requirements](#multiple-requirements)
  * [ART Object Config](#art-object-config)
    * [Actions](#actions)
    * [Requirements](#requirements)
    * [Trigger](#trigger)

## Getting started

To use the [ART-Framework](https://github.com/silthus/art-framework) on your server, you have to do two things:

1. [Download the latest release](https://github.com/silthus/art-framework/releases/latest) and install the ART plugin for your Minecraft distribution.
2. [Learn the syntax](#art-config-syntax) for configuring **a**ctions, **r**equirements and **t**rigger.
3. Install a plugin that supports loading ART configs and follow the instructions of the plugin where you should put the [ART section](#art-config-syntax).

## ART Config Syntax

You just need to learn this once and can use your wisdom for any plugin that supports ART. *Cool isn't it? :)*

### ART Types

Let's start with the three basic `ART Types`:

* **Actions** can be used to do stuff to your players, entities, blocks, you name it.  
  All actions start with an exclamation mark: <kbd>!</kbd>.
* **Requirements** can be used to check conditions before executing actions or reacting to triggers.  
  All requirements start with an question mark: <kbd>?</kbd>.
* **Trigger** can be used to react to events that are happening in your world.  
  All trigger start with an at sign: <kbd>@</kbd>.

*How can I know what ART is loaded on my server?*

There are several methods to find out all useable ART.  
But the quickest way is to **look in the startup log** of your server.  
There all ART that can be used on your server will be listed including their config parameters.

*How can I use this information to create my ART config?*

### Basic Example

Once you know what ART you can use and where to put it you just create a list of strings like in the following example.

```yaml
# The plugin called "foobar" told you in their documentation
# that you need to put the ART config inside the "foobars_art" section.
foobars_art:
    # this is the ART config section
    # it will look the same across all plugins

    # there should be an automatically generated id
    # do not change this unless you copied a config
    # if you did delete the line and let it regenerate
    id: 3f7d956f-64f1-44f9-bf48-0308d6e30752
    # find out more about the ART options below
    options:
        worlds: []
    # this is where the fun part starts
    # you can write your ART config as list of strings under the "art" section
    art:
        # this trigger will only trigger the actions and requirements after it
        # if the player is in a radius of 10 around the given coordinates
        - '@player.move x=5, y=10, z=-22, radius=10'
        # this requirement will only be check if the trigger above matched
        # meaning the player is at the given coordinates
        # then we check if the players health is below 50% of his max health
        - '?player.health <=0.5 percentage=true'
        # if the players health is below 50% and he is at the given coordinates
        # teleport him to the defined coordinates (spawn or something)
        - '!player.teleport 0,128,0,world'
        # after that heal the player for 10 hitpoints
        - '!player.heal 10'
        # and send him a text message
        - '!text "You have been saved!"'
```

### Multiple Requirements

You can combine multiple requirements in one ART config. All you need to do is write them directly after each other.  
This will make sure all of your requirements are checked and only execute the underlying actions if all of them pass.

> Multiple requirements are combined in an AND statement.  
> This means all requirement checks must be successfull.

```yaml
...
art:
    # will trigger when the player opens any chest
    - '@chest.open'
    # checks if the player is online more than 1 hour
    - '?player.online >1h'
    # and if the chest is empty
    - '?chest.empty'
    # fills it with some diamonds but only once every 24 hours
    - '!chest.fill[cooldown=24h] diamond amount=10'
```

This advanced example shows how powerfull ART can be and also introduced a new configurable part. The [ART Object Config](#art-object-config) inside the square brackets <kbd>[ ]</kbd>.

### ART Object Config

Each [ART Type](#art-types) has its own `ART Object Config` that can be used to control the executing or check of the ART.

> The `ART Object Config` must be placed directly after the identifier into square brackets <kbd>[ ]</kbd>.  
> For example: `!teleport[delay=5s] 1,2,3` teleports the player after five seconds.

<details>
<summary>These config options are coming soon...</summary>

#### Actions

All actions have the following config options.

| Option | Default | Description |
| :------ | ------- | ----------- |
| cooldown | `0s` | Time to wait between each execution of the action, *e.g.: `1y2m3d10h5m1s10` waits 1 year 2 months 3 days 10 hours 5 minutes 1 second and 10 ticks.* |
| delay | `0s` | Time to wait before executing the action. |
| execute_once | `false` | Set this to `true` to execute the action only once per target. |

#### Requirements

All requirements have the following config options.

| Option | Default | Description |
| :----- | ------- | ----------- |
| persistent | `false` | Set to true if you want to persist the result of the requirement check accross server restarts. |
| count | `0` | How many times must this requirement check be successfull for it to become true. |

#### Trigger

All trigger have the following config options.

| Option | Default | Description |
| :----- | ------- | ----------- |
| persistent | `false` | Set to true if you want to persist the result of the trigger checks accross server restarts. |
| count | `0` | How many times must this trigger be triggered before its actions are executed. |
| execute_once | `false` | Set this to `true` to execute the trigger only once per target. |
| cooldown | `0s` | Time to wait between each execution of the trigger, *e.g.: `1y2m3d10h5m1s10` waits 1 year 2 months 3 days 10 hours 5 minutes 1 second and 10 ticks.* |
| delay | `0s` | Time to wait before executing the trigger. |

</details>
