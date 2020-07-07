# Requirements

Requirements can be used to check conditions before executing [actions](actions.md) or reacting to [triggers](trigger.md).  
All requirements start with an question mark: <kbd>?</kbd>.

The syntax for requirements looks like this:

```text
?requirement-name[config_options] requirement_options
```

## Config Options

All requirements have the following config options.

| Option | Default | Description |
| :----- | :------- | :----------- |
| count | `0` | How many times must this requirement check be successfull for it to become true. |
| negated | `false` | Set this to `true` to reverse the outcome of the requirement check. |
| check_once | `false` | Set this to `true` to store the result of the first requirement check in the database. |

## Combining multiple Requirements

You can combine multiple requirements in one ART config. All you need to do is write them directly after each other.  
This will make sure all of your requirements are checked and only execute the underlying actions if all of them pass.

> Multiple requirements are combined in an AND statement.  
> This means all requirement checks must be successfull.

In this example, the chest must be empty **and** the player must be online for more than one hour.

```yaml
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

## Examples

All of these example requirements are provided by the [basic requirements package](../plugins.md) for Bukkit.

### Checking the absolute location of a player

This requirement checks if the player is around 10 blocks of the `x=0,y=128,z=0` location in the `world`. From the docs of the requirement we know that we need to set `zeros=true` if we use any `0` coordinates.

```yaml
art:
  - '?location 0,128,0,world radius=10 zeros=true'
```
