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
