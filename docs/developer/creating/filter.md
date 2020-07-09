# Filtering the ART Result

There may come the need to apply global or local filters when [creating](creating_art.md) or [using](using_art.md) ART. This could be filtering out all instances of `Player` that are NPCs or only applying certain ART to users that have a defined permission.

No matter the use case, filters have you covered when you want to add specific [requirements](requirements.md) to your [ArtResult](using_art.md).

* [Creating a filter](#creating-a-filter)
* [Registering a global filter](#registering-a-global-filter)
* [Using a local filter](#using-a-local-filter)
* [Other Resources](#other-resources)

## Creating a filter

To create a filter you just need to implement the `Filter<TTarget>` interface or provide it as a lambda expression.

> Filters can only be scoped to one target type at a time.

When creating a filter you only have access to the target and the `ArtConfig` that was used to create the `ArtResult`.

```java
public class NpcFilter implements Filter<Entity> {

    // return true to keep the target in the result set
    // and return false to filter it out
    @Override
    public boolean test(Target<Entity> target, ArtConfig config) {
        Entity player = target.getSource();

        return !entity.hasMetadata("NPC");
    }
}

```

## Registering a global filter

If you want to use your filter as a global filter, meaning it is applied to all plugins that use ART, then you need to [register it](creating_art.md) together with your ART at the start of your plugin or [ART Module](modules.md).

> Make sure to check if the ART plugin is loaded before your register your filter.  
> See the [creating ART documentation](creating_art.md) for more details on how to register your ART.

```java
ART.register(ArtBukkitDescription.ofPlugin(this), artBuilder -> artBuilder
        .target(Entity.class)
            .filter(new NpcFilter());
```

## Using a local filter

The other option is to use your filter locally on a `ArtResult`. This will only apply the filter to this one ArtResult method invocation.

```java
ArtResult result = ART.load(config);
// filters can be applied when testing requirements
if (result.test(player, (target, config) -> !target.getSource().hasMetadata("NPC"))) {
    ...
}
// or when executing actions
result.execute(player, (target, config) -> !target.getSource().hasMetadata("NPC")));
```

## Other Resources

* [General Developer API Documentation](index.md)
* [Creating Actions Requirements Trigger](creating_art.md)
* [Using ART inside your plugin](using_art.md)
