# **A**ctions **R**equirements **T**rigger (ART) Framework

[![Build Status](https://github.com/Silthus/art-framework/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![Documentation Status](https://readthedocs.org/projects/art-framework/badge/?version=latest)](https://art.silthus.net/en/latest/?badge=latest)
[![codecov](https://codecov.io/gh/Silthus/art-framework/branch/master/graph/badge.svg)](https://codecov.io/gh/Silthus/art-framework)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Silthus/art-framework?include_prereleases&label=release)](../../releases)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

This framework enables you to easily provide actions, requirements and triggers that can be used in configs for any number of plugins.

This framework is for [**DEVELOPERS**](https://art-framework.rtfd.io/page/developer/) and [**SERVER ADMINS**](https://art-framework.rtfd.io/page/admin/).
Each have their own documentation and benefits, see [the documentation](https://art-framework.rtfd.io/) for more details.

* [Getting started](#getting-started)
* [Features](#features)
* [Roadmap](#roadmap)

## Getting started

You can find all of the documentation on the project homepage: [art.silthus.net](https://art-framework.rtfd.io/).
But to make your life easier and give you some guidence there are two quickstart guides. One for admins and one for developers.

* [Quickstart Guide for Server Admins](https://art-framework.rtfd.io/page/admin/quickstart)
* [Quickstart Guide for Developers](https://art-framework.rtfd.io/page/developer/quickstart)

## Features

The **ART-Framework** was built with one goal in mind:

> Make **sharing** of **actions**, **requirements** and **trigger** accross plugins super easy, without depending on them.

*Okay, but what does this mean exactly?*

Let's take the following example:

> You have a plugin and want to add a feature that rewards players for doing stuff.  
> You want the rewards to be configurable by admins but don't want to depend on too many plugins.

This is where the ART-Framework comes in. Just like [Vault](https://github.com/MilkBowl/Vault) it provides a possibility for plugins to use any number of other plugins without directly depending on them.

*Well I got that, but what does the config look like?*

It looks like this:

```yaml
# you can use ART inside any of your configs mixed with all of you config stuff
...
# then put an ART section somewhere, which you can load inside your plugin
# see the developer documentation for more details on how to do that
rewards:
  art:
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
ARTResult result = ART.load(config);

// use it somewhere in your plugin, e.g. in an event or command
result.execute(player);

// or test if all requirements are met
if (result.test(player)) {
  // do stuff
}
```

## Roadmap

[Open an issue](https://github.com/Silthus/art-framework/issues/new/choose) if you want more features or found a bug.

* Actions
  * ~~Use delays and other config artObjectMeta for more control~~
* Requirements
  * ~~Check anything anywhere~~
  * Expression syntax for advanced scenarios  
    `(?player.health >10) || ((?mythicmobs:mob.kill[count:>5,persistent:true] super_omega_boss) && (?player.health <5))`
  * Persistant checks and meta data storage, e.g. for counting mob kills
* Trigger
  * ~~React to stuff happening in your world~~
  * ~~Can have requirements and actions attached to them~~  
    `?worldguard:region spawn`  
    `@player.move[cooldown=24h]`  
    `!text &7Remember to vote for our server!`
  * ~~Can have cooldown, delay, and much more~~
  * ~~Persistent meta data storage accross server reboots~~
* Central **web app** that provides a searchable list of all plugins and their registered ART.  
  You can find details about every action, requirement, trigger, their config including a description and more.
