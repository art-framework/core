Variables can be used to store temporary data or provide automatic replacements before the config is parsed. They are shared during the lifetime of an [ArtContext](art-context.md) and available to all art called by the execution context.

`String` and primitive type variables (`int`, `boolean`, `double`, etc.) will automatically replace any placeholder] with the following syntax: `${key}`.

Here is a quick example of an automatic variable replacement.

```java
ArtContext context = ART.load(Arrays.asList(
    "@move ${my-var}"
));
context.var("my-var", "1, 2, 3");

ART.trigger(PlayerMoveTrigger.class).with(player).execute();

// @move ${my-var} becomes @move 1, 2, 3
```

## Replacements

Additionally to the automatic variable replacements, you have the option to register your own custom replacements, e.g. implementing a [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) module.

[PlaceholderReplacement.java](https://raw.githubusercontent.com/art-framework/art-placeholderapi/main/src/main/java/io/artframework/modules/placeholderapi/PlaceholderReplacement.java ':include')

[ArtPlaceholderApiPlugin.java](https://raw.githubusercontent.com/art-framework/art-placeholderapi/main/src/main/java/io/artframework/modules/placeholderapi/ArtPlaceholderApiPlugin.java ':include')
