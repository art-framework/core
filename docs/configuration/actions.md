# Actions

Actions can be used to do stuff to your players, entities, blocks, you name it.  
All actions start with an exclamation mark: <kbd>!</kbd>.

The syntax for actions looks like this:

```text
!action-name[config_options] action_options
```

## Config Options

| Option | Default | Description |
| :------ | ------- | ----------- |
| cooldown | `0s` | Time to wait between each execution of the action, *e.g.: `1y2m3d10h5m1s10` waits 1 year 2 months 3 days 10 hours 5 minutes 1 second and 10 ticks.* |
| delay | `0s` | Time to wait before executing the action. |
| execute_once | `false` | Set this to `true` to execute the action only once per target. |

## Execution Order Rules

Actions are executed **sequentially** and grouped together using the same [requirements](/configuration/requirements.md) and [triggers](/configuration/trigger.md). See the following examples for a detailed explanation on all of the different rules that apply when the execution of actions is evaluated.

### Shared Action Context

!> All actions that directly follow an action are executed in the same context as their parent action.

```yaml
art:
    # both actions will have the same requirement
    # and the player is first damaged and then a message will be sent
    # this all happens in one tick (1/20th second)
    - '?location 10,20,30'
    - '!damage 10'
    - '!text "You have been damaged."'
```

### Separate Requirements

!> Actions always use the requirements that are directly above them, respecting the [shared context rule](#shared-action-context).

```yaml
art:
    # these two actions both have their own requirements
    - '?location world=world'
    # this action is only executed if the player is inside the "normal" world
    - '!damage 10'
    # the shared context rule still applies
    - '!text "You have been damaged."'
    - '?location world=world_nether'
    # and this action is only executed if he is in the nether
    - '!damage 20'
```

### Multiple Triggers

!> Actions can have multiple [triggers](/configuration/trigger.md) and will execute if any of them fire.

```yaml
art:
    # give the player a golden apple the first time he dies
    # and every 48 hours after he logs in.
    - '@player.respawn[execute_once=true]'
    - '@player.login[cooldown=48h]'
    - '!item.add golden_apple'
    - '!text "Here is something to help you out."'
```

## Examples

All of these example actions are provided by the [basic ART package](https://github.com/art-framework/art-bukkit) for Bukkit.

> [!NOTE]
> Most of these examples combine the use of [Actions](/configuration/actions.md), [Requirements](/configuration/requirements.md) and [Trigger](/configuration/trigger.md).
> See the corresponding documentation for more information about each of them.

### Teleport the player when he enters a location

This could be useful as an alternative to portals or if you want to teleport players that go somewhere you don't want then to go.

```yaml
art:
    # Teleport the player to the nether when he comes near the spawn
    - '@player.move 0,128,0,world radius=10 zeros=true'
    - '!teleport 0,128,0,world_nether'
    # also heal the player for his maximum health
    # but only every 24 hours
    - '!heal[cooldown=24h] max=true'
```

### Configurable rewards

The ART-Framework can be used by many other plugins to allow server admins the maximum flexibility when configuring rewards and a lot of other stuff. Here is just one example of this using the popular [EssentialsX](https://github.com/EssentialsX/Essentials) plugin. In this example we wrap the `/kits` command into a `!cmd` action. This could potentially also be an own `!essentials:kit` action if you ask the author to add support for the [ART-Framework](https://github.com/art-framework/art-framework).

```yaml
art:
    # give the player a kit every 24h
    - '@player.join[cooldown=24h]'
    # and if he played longer than 4 weeks give him a special kit
    - '?playtime >4w'
    # this shows the use of the generic cmd action and the player placeholder
    - '!cmd "/kit yourkit %{player}"'
    - '?playtime <4w'
    # this is the alternative using a native action provided by EssentialsX (which may come in the future)
    - '!essentials:kit your_starter_kit'
```
