# Trigger

Trigger can be used to react to events that are happening in your world.  
All trigger start with an at sign: <kbd>@</kbd>.

The syntax for trigger looks like this:

```text
?trigger-name[config_options] trigger_options
```

## Config Options

All trigger have the following config options.

| Option | Default | Description |
| :----- | ------- | ----------- |
| count | `0` | How many times must this trigger be triggered before its actions are executed. |
| execute_once | `false` | Set this to `true` to execute the trigger only once per target. |
| cooldown | `0s` | Time to wait between each execution of the trigger, *e.g.: `1y2m3d10h5m1s10` waits 1 year 2 months 3 days 10 hours 5 minutes 1 second and 10 ticks.* |
| delay | `0s` | Time to wait before executing the trigger. |
| execute_actions | `true` | Set this to false to prevent the trigger from executing actions attached to it. |

