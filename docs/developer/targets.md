# Targets

Every [Action](actions.md), [Requirement](requirements.md) and [Trigger](trigger.md) takes a `Target<Type>` instead of a direct `Player` reference as an argument.
Normally you won't notice this as the `Target` is automatically wrapped for you when calling methods in a trigger or the [ArtContext](art-context.md).

> **Why does ART take a Target<?> object instead of a direct reference?**  
> One of the biggest reasons for this is **extensibility**, the option to improve or change the target implementation later, without breaking every plugin that uses the ART-Framework. Other reasons are:
>
> * The option to use a consistent unique identifier accross different target types.
> * Storing [temporary data](data.md) on the target during the lifetime of the execution.
> * Option to extend the target with additional functions like a `MessageSender` interface.

The target for subsequent actions or requrirements is always driven by the initial call to a [Trigger#trigger(...)](trigger.md). The ART-Framework tries to wrap the given source object into a target wrapper. If that fails a log message will be written and the trigger will silently fail.

!> You need to create a [custom target wrapper](#creating-custom-target-types) if you see the following log message in the console.
    `Unable to find target wrapper for 'your.package.and.target.source'.`

## Built-in Target Types

The art-framework comes with several built in targets. If this is not enough for you, you can always create your own [target wrapper](#creating-custom-target-types).

* **Player**: Wraps the default bukkit `Player`. Can also receive messages.
* **OfflinePlayer**: Wraps an `OfflinePlayer`. Uses the same unique identifier as the `Player`.
* **LivingEntity**: Wraps any living entity into a target. Will use the same unique identifier as the `Player` if it is a player.
* **Entity**: Wraps any entity as a target. Will use the same unique identifier as the `Player` if it is a player.

Take a look at the [`io.artframework.bukkit.targets`](https://github.com/art-framework/art-core/tree/master/bukkit/src/main/java/io/artframework/bukkit/targets) package for a complete list of all built-in bukkit target types.

## Creating Custom Target Types

There may come the time when the built-in target types are not enough and you need to create your own target wrapper. For that you just need to extend `AbstractTarget<Type>` or implement `Target<Type>`.

!> Make sure that `uniqueId()` always returns the same unique identifier for the same target source.

```java
public class PlayerTarget extends AbstractTarget<Player> implements MessageSender {

    public PlayerTarget(Player source) {
        super(source);
    }

    @Override
    public String getUniqueId() {
        // make sure this always returns the same unique identifier for the same source
        return getSource().getUniqueId().toString();
    }

    // you can optionally implement MessageSender to let the target receive messages
    @Override
    public void sendMessage(String... message) {
        getSource().sendMessage(message);
    }
}
```
