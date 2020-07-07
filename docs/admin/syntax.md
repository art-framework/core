# ART Config Syntax

You just need to learn this once and can use your wisdom for any plugin that supports ART. *Cool isn't it? :)*

## ART Types

Let's start with the three basic `ART Types`:

* [**Actions**](actions.md) can be used to do stuff to your players, entities, blocks, you name it.  
  All actions start with an exclamation mark: <kbd>!</kbd>.
* [**Requirements**](requirements.md) can be used to check conditions before executing actions or reacting to triggers.  
  All requirements start with an question mark: <kbd>?</kbd>.
* [**Trigger**](trigger.md) can be used to react to events that are happening in your world.  
  All trigger start with an at sign: <kbd>@</kbd>.

*How can I know what ART is loaded on my server?*

There are several methods to find out all useable ART.  
But the quickest way is to **look in the startup log** of your server.  
There all ART that can be used on your server will be listed including their config parameters.

*How can I use this information to create my ART config?*

See below for a basic [syntax example](#syntax-example) on how to use ART objects in your config. You can also look at the individual documentation for [**A**ctions](actions.md), [**R**equirements](requirements.md) and [**T**rigger](trigger.md).

## Syntax Example

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

## ART Object Config

Each [ART Type](#art-types) has its own `ART Object Config` that can be used to control the executing or check of the ART.

> The `ART Object Config` must be placed directly after the identifier into square brackets <kbd>[ ]</kbd>.  
> For example: `!teleport[delay=5s] 1,2,3` teleports the player after five seconds.

Look at each of the documentation for [**A**ctions](actions.md), [**R**equirements](requirements.md) and [**T**rigger](trigger.md) to learn more about the individual config options.
