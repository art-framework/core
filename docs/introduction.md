[![Build Status](https://github.com/art-framework/art-framework/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![Netlify](https://img.shields.io/netlify/dae1bdab-7c51-4172-97ab-f8fdd8f0612e?label=docs)](https://art-framework.io)
[![codecov](https://codecov.io/gh/art-framework/art-framework/branch/master/graph/badge.svg?token=Ex9gV4AfK8)](https://codecov.io/gh/art-framework/art-framework)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/art-framework/art-framework?include_prereleases&label=release)](../../releases)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

The **ART-Framework** is a java library primarly developed for the use in Minecraft plugins.  
It provides a way to define various **workflows** using **A**ctions, **R**equirements and **T**rigger (ART) using a **powerful but simple** configuration syntax and yaml files.

## Getting started

The `art-framework` has three major use cases and each of them have their own documentation. Take a look at the following overview to get a feeling where you need to start.

* **[Creating ART](/developer/)** - `DEVELOPER`  
  This guide is for you if you are a developer and want to provide actions, requirements or triggers from your plugin to others that use the art-framework.  
  *For example: a plugin, e.g. vault, wants to provide actions to give players money.*
* **[Using ART](/developer/)** - `DEVELOPER`  
  This guide is for you if you want to use the art-framework in your plugin to provide flexiable configuration options to your users.  
  *For example: your plugin needs flexible rewards and wants to use the art-framework to configure those rewards.*
* **[Configuring ART](/configuration/)** - `SERVER ADMINS`  
  This guide is for your if you are a server admin and want to learn the art-syntax to configure plugins that use the art-framework.  
  *For example: you saw the config example below and want to know how to write more of this awesome stuff.*

## Usage Example

The power of the framework comes from the ability to share and reuse ART across plugins without directly depending on them. Take the following *advanced example* of an ART configuration:

> [!NOTE]
> In this example the player will receive a one time starter kit when he enters a defined area (spawn) if he has played less then one hour. If he has a play time of more than one hour he will receive ten diamonds every time he enters the spawn area. Everything has a cooldown of 48 hours. So the player can only receive ten diamonds every two days.

```yaml
art:
  # the @ represents a trigger
  # in this case a location trigger scoped to the defined coordinates and a radius of 10
  # the trigger also has a cooldown of 48h and will only execute the underlying actions every 48h
  - '@location(cooldown:48h) 10,128,10 radius:10'
  # the ! represents an action
  # here we give the player some money thru the vault plugin
  - '!vault:money.add 100'
  # the ? represents a requirement
  # we simply check if the player played more than one hour on the server
  # if the requirement check is successful the underlying action will be executed
  - '?player.play-time <1h'
  # this is an example of an action provided by an other plugin, in this case essentialsx
  - '!essentialsx:kit(execute_once:true) starter-kit'
  # and then we simply send the player two messages, each on its own line
  - '!txt "Welcome %{player}, take this starter kit as a gift to make your journey easier on our server.", "You can also come back in two days to receive some other goodies we have in store for you :)"'
  - '?player.play-time > 1h'
  # the number of actions is just limited to the imagination of the developers and plugin owners that provide those actions
  # here we are using a default action that is built into the art framework bukkit implementation
  - '!item.add diamond amount:10'
  - '!txt "Here are some diamonds for your loyalty."'
```

You can find the detailed [configuration documentation here](configuration).

---

And now take a look at the developer side of things. In this example we are going to show how the text action is implemented.

[SendMessageAction.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/actions/SendMessageAction.java ':include :fragment=demo')

> [!TIP]
> That's it! Your action can now be used in any art-configuration.
