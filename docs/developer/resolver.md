# Resolver

Resolver can be used to resolve custom objects inside [Actions](actions.md), [Requirements](requirements.md) and [Trigger](trigger.md). An example of this would be enums or an location.

The following code will automatically resolve the item by its name using the built-in `MaterialResolver`.

```java
@ConfigOption
@Resolve
private Material item;
```

!> The value can be null if `required` is not set in the `@ConfigOption` annotation of the field.

## Custom Resolver

You can register your own `Resolver` by implementing the `Resolver<TType>` interface and registering it with the scope: `scope.register().resolvers().add(MyResolver.class)`. Here is an example on how the `MaterialResolver` is implemented.

[MaterialResolver.java](https://raw.githubusercontent.com/art-framework/core/master/bukkit/src/main/java/io/artframework/bukkit/resolver/MaterialResolver.java ':include')
