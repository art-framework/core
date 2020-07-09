# Handling Data

There comes the time when you need to store or provide data in your [**A**ctions](actions.md), [**R**equirements](requirements.md) and [**T**rigger](trigger.md).  
The ART-Framework currently supports the two following scenarios:

* Store the data for yourself in a persistent manner.
  *e.g.: you have a requirement that needs to store the count for player*
* Provide data to other ART executed in the same context.
  *e.g.: you want to provide the amount of items added to a players inventory so it can be used in a text action following your action*

## Storing data for later

The `ArtContext` that is provided in your `ArtObject` can be used to store and retrieve data. How the data is stored will depend on the configuration of the server admin.
You store the data by providing the `Target` and a unique storage `key`. All data you pass to the `store(...)` method will be serialized as JSON if possible.
That means you can store just about anything and are not limited to primitive values.

!!! important
    Make sure that the object you are storing has a parameterless constructor for deserialization of your data.

Here is an example of a `CountRequirement` that uses the data store to persist the counter.

```java
@Name("count")
@Description({
        "This requirement returns true once it has been checked as often as defined in the count.",
        "You also have some additional options to send messages to the player informing him about the counter."
})
@Config(CountRequirement.Config.class)
public class CountRequirement implements Requirement<Object, CountRequirement.Config> {

    // you should create constants for your keys to keep your code organized
    private static final String COUNTER_KEY = "count";

    @Override
    public boolean test(Target<Object> target, RequirementContext<Object, Config> context) {

        // here we retrieve the data from the store
        // you can retrieve data even if it does not exist and will get an empty optional back
        final int currentCount = context.get(target, COUNTER_KEY, Integer.class).orElse(0) + 1;
        // then we use the context to store our data
        context.store(target, COUNTER_KEY, currentCount);

        return context.getConfig().map(config -> config.count <= currentCount).orElse(true);
    }

    public static class Config {

        @Required
        @Position(0)
        @Description("Set how often this requirement must be checked before it is successful.")
        private int count;
    }
}
```
