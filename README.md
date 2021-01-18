# **A**ctions **R**equirements **T**rigger (ART) Framework

[![Build Status](https://github.com/Silthus/art-framework/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![Netlify](https://img.shields.io/netlify/dae1bdab-7c51-4172-97ab-f8fdd8f0612e?label=docs)](https://art-framework.io)
[![jitpack status](https://jitpack.io/v/io.art-framework/art-core.svg)](https://jitpack.io/#io.art-framework/art-core)
[![codecov](https://codecov.io/gh/art-framework/core/branch/master/graph/badge.svg?token=Ex9gV4AfK8)](https://codecov.io/gh/art-framework/core)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Silthus/art-framework?include_prereleases&label=release)](../../releases)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

This framework enables you to easily provide actions, requirements and triggers that can be used in configs for any number of plugins.

* [Getting started](#getting-started)
* [Features](#features)

## Getting started

You can find all the documentation on the project homepage: [art-framework.io](https://art-framework.io/).

## Features

The **ART-Framework** was built with one goal in mind:

> Make **sharing** of **actions**, **requirements** and **trigger** accross plugins super easy, without depending on them.

*Okay, but what does this mean exactly?*

Let's take the following example:

> You have a plugin and want to add a feature that rewards players for doing stuff.  
> You want the rewards to be configurable by admins but don't want to depend on too many plugins.

This is where the ART-Framework comes in. Just like [Vault](https://github.com/MilkBowl/Vault) it provides a possibility for plugins to use any number of other plugins without directly depending on them.

*Ok I got that, but what does the config look like?*

It looks like this:

```yaml
# you can use ART inside any of your configs mixed with all of you config stuff
...
# then put an ART section somewhere, which you can load inside your plugin
# see the developer documentation for more details on how to do that
rewards:
  # no need to depend on vault directly to give players money
  - '!vault:player.money.add 20'
  # you want to add some custom items from an unknown plugin?
  # no problem!
  - '!my-custom-item-plugin:player.item.add mighty-sword_1337, amount=5'
  # every reward has a cost!
  # damage the player for 10 hitpoints
  # but only if his health is above 15 hitpoint
  - '?player.health >=15'
  - '!player.damage damage=10'
```

And this is just using actions for rewards. We dind't even get to the requirements and trigger yet :)

*And how does it look on the developer side?*

It is as easy as parsing a config and creating your ART. After that you can use the actions anywhere you want.

```java
// parse the config and get your art result
ArtContext art = ART.load(config.getStringList("rewards"));

// execute all configured actions
result.execute(player);

// test if all requirements are met
if (result.test(player)) {
  // do stuff
}

// or listen to triggers
art.enableTrigger();
art.onTrigger((target, context) -> {
    
});
```
