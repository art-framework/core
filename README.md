# **A**ctions **R**equirements **T**rigger (ART) Framework

[![Build Status](https://github.com/art-framework/art-framework/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![Netlify](https://img.shields.io/netlify/dae1bdab-7c51-4172-97ab-f8fdd8f0612e?label=docs)](https://art-framework.io)
[![jitpack status](https://jitpack.io/v/io.art-framework/art-framework.svg)](https://jitpack.io/#io.art-framework/art-framework)
[![codecov](https://codecov.io/gh/art-framework/art-framework/branch/master/graph/badge.svg?token=Ex9gV4AfK8)](https://codecov.io/gh/art-framework/art-framework)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/art-framework/art-framework?include_prereleases&label=release)](../../releases)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

This framework enables you to easily provide actions, requirements and triggers that can be used in configs for any number of plugins. One very common use case would be to support the art-framework additionally to executing commands for giving rewards or doing stuff when something in your plugin happens.

<p align="center">
  <img height="250px" src="docs/_media/logo.png"><br/>
  <a href="https://art-framework.io" target="_blank"><img src="docs/_media/text_only_docs.png" height="20px"/></a>&nbsp;<img height="20px" src="docs/_media/spacer.png"/>&nbsp;<a href="releases/" target="_blank"><img src="docs/_media/text_only_spigot.png" height="20px"/></a>&nbsp;<img height="20px" src="docs/_media/spacer.png"/>&nbsp;<a href="https://jdocs.art-framework.io" target="_blank"><img src="docs/_media/text_only_javadocs.png" height="20px"/></a>
</p>

## Features

* Easy to use API. `ART.load(...)` is all you need!
* Support for [multiple parsers](https://art-framework.io/#/developer/parser) and one built in `flow-parser`.
* Easy to learn configuration syntax: `!action`, `?requirement`, `@trigger`.
* Classpath scanning to remove tedious configuration code.
* Extremly [modular](https://art-framework.io/#/developer/modules). Every provider and configuration can be changed.
* Platform independent, can be implemented for use in Minecraft plugins or any other software.  
  Available platforms: [Bukkit/Spigot/Paper](https://art-framework.io/#/platforms/bukkit)
* Enhanced command usage: `/minecraft:give(cooldown:4h) ${player} golden_apple 5`
* [PlaceholderAPI support](https://github.com/art-framework/art-placeholderapi): `!money.add %vault_eco_balance%` doubles the players balance.

## Example

You have a plugin that allows the configuration of executable commands when an action in your plugin occurs, e.g. for giving rewards.

One of many use cases of the art-framework is to empower exactly those plugins that use commands as a compability layer between other plugins.

Take the following art-example:

```yaml
# you can use ART inside any of your configs mixed with all of you config stuff
...
# then put an ART section somewhere, which you can load inside your plugin
# see the developer documentation for more details on how to do that
rewards:
  # no need to depend on vault directly to give players money
  - '!vault:player.money.add 1000'
  # you want to add some custom items from an unknown plugin?
  # no problem!
  - '!my-custom-item-plugin:player.item.add mighty-sword_1337, amount=5'
  - '?permission ranks.donor'
  # fallback to using commands enhanced with the power of the art-framework
  - '/goldencrates(cooldown:24h) givekey art-somekey'
  # every reward has a cost!
  # damage the player for 10 hitpoints
  # but only if his health is above 15 hitpoint
  - '?player.health >=15'
  - '!player.damage damage=10'
```

That was the configuration side, and here is how you can implement exactly that in your plugin:

```java
try {
  // parse the config and get your art result
  ArtContext rewards = ART.load(config.getStringList("rewards"));
  // execute all configured rewards
  result.execute(player);
} catch (ParseException e) {
  // invalid art configuration
}
```

That is for loading and [using art](https://art-framework.io/#/developer/). Here is a veriy basic example on how to create art, e.g. the `vault:player.money.add` action.

```java
@ART(
  value = "vault:player.money.add",
  description = "Adds the given amount of money to the player. Can be an offline player.",
  alias = {"money.add", "player.money.add", "money", "vault:money.add", "vault:money"}
)
public class AddMoneyAction implements Action<OfflinePlayer> {

  private final Economy economy;

  public AddMoneyAction(Economy economy) {
    this.economy = economy;
  }

  @ConfigOption(
    required = true,
    position = 0,
    description = "The amount of money that should be added to the player."
  )
  private int amount;

  @Override
  public Result execute(@NonNull Target<OfflinePlayer> target, @NonNull ExecutionContext<ActionContext<OfflinePlayer>> context) {

    economy.depositPlayer(target.source(), amount);

    return success();
  }
}
```

## Roadmap

The following is planned for the future of the art-framework:

* Online editor with auto completion support
* Public registry of art-modules
* Auto downloading of required art-modules from the registry
