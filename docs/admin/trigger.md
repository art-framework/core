

| Option | Default | Description |
| :----- | ------- | ----------- |
| persistent | `false` | Set to true if you want to persist the result of the trigger checks accross server restarts. |
| count | `0` | How many times must this trigger be triggered before its actions are executed. |
| execute_once | `false` | Set this to `true` to execute the trigger only once per target. |
| cooldown | `0s` | Time to wait between each execution of the trigger, *e.g.: `1y2m3d10h5m1s10` waits 1 year 2 months 3 days 10 hours 5 minutes 1 second and 10 ticks.* |
| delay | `0s` | Time to wait before executing the trigger. |