The three primary annotations you need to remember are the `@ART`, `@ConfigOption` and `@ArtModule` annotation. The [javadocs](https://jdocs.art-framework.io/io/artframework/annotations/package-summary.html) explains the usage of all annotations pretty well, but here is a summary for you to look up.

<!-- panels:start -->
<!-- div:title-panel -->
## @ART

Every [action](actions.md), [requirement](requirements.md) and [trigger](trigger.md) must be annotated with the [`@ART`](https://jdocs.art-framework.io/io/artframework/annotations/ART.html) annotation to provide a unique identifier and additional meta data about the art-object.

> All of the following metadata is optional, except for the value (identifier).  
> However it is highly recommended to provide as much information as possible about your art-object. The data might be used for a public registry of art-modules in the future.

<!-- div:left-panel -->

| Setting | Default | Description |
| ------- | ------- | ----------- |
| `value` |         | The required unique identifier of the art-object. It is recommended to prepend the name of the plugin separated by a colon and then followed by the art-object identifier. |
| `alias` | `[]`    | A list of aliases for your art-object. They can be used instead of the identifier, but do not fail to register your art-object if an alias already exists. |
| `description` | `[]` | An optional multiline description of your art-object. Tell the user what it does and how and when to use it. |
| `autoRegister` | `true` | Set the value to false to prevent the art-framework from auto registering your art-object if it is loaded by a class path scanner. |

<!-- div:right-panel -->

[DamageLivingEntityAction.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/actions/DamageLivingEntityAction.java ':include :fragment=header')

<!-- div:title-panel -->
## @ConfigOption

You can annotate properties of your class with the [`@ConfigOption`](https://jdocs.art-framework.io/io/artframework/annotations/ConfigOption.html) annotation to have them provided by the [parser](parser.md) when your art-object is loaded. See the seperate [config chapter](config.md) for more details on loading and defining configuration options.

<!-- div:left-panel -->

| Setting | Default | Description |
| ------- | ------- | ----------- |
| `value` | `<field_name>` | By default the lowercase_underscore name of the field (property) is used as the config option identifier. Using the `value` of the annotation you can specify a custom identifier. |
| `description` | `[]` | Every config option should have a precise description for the end user on what the setting does. |
| `position` | `-1` | Set a position, starting at `0`, to allow parsing of your art-object config with positioned parameters. |
| `required` | `false` | Set to `true` to make the config option required. If your art-object has missing required fiels it will not load. On the other hand non required fields may be null. |

<!-- div:right-panel -->

[DamageLivingEntityAction.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/actions/DamageLivingEntityAction.java ':include :fragment=config')

<!-- div:title-panel -->

## @ArtModule

The [`@ArtModule`](https://jdocs.art-framework.io/io/artframework/annotations/ArtModule.html) annotation is required on all plugins or modules that should be recognized as [art-modules](modules.md). Currently only the identifier is required and used for the dependencies of other modules. In the furute the metadata of the annotations may be used in a public registry where you can pick and download modules on demand.

<!-- div:left-panel -->

| Setting | Default | Description |
| ------- | ------- | ----------- |
| `value` | | A required unique identifier of your module. The identifier is used in the `depends` setting of other modules.
| `description` | `[]` | A multiline description of what your module provides and does. This helps users decide if they want to use your module or not. |
| `version` | `1.0.0` | A [semantic version](https://semver.org/) of your module. Currently this has no effect, but might be useful in the future when the registry launches.
| `depends` | `[]` | A list of dependencies of your module, e.g. `"module:art-bukkit", "plugin:Vault"`. |

<!-- div:right-panel -->

[ArtBukkitModule.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/ArtBukkitModule.java ':include :fragment=module')

<!-- div:title-panel -->

## @Resolve

Use the [`@Resolve`](https://jdocs.art-framework.io/io/artframework/annotations/Resolve.html) annotation on config fields in your art-objects to use a [resolver](resolver.md) to resolve the object when your art-object is called.

<!-- div:left-panel -->

| Setting | Default | Description |
| ------- | ------- | ----------- |
| `value` | `[]` | Takes a valid resolver class that should be used to resolve the type of the field. |

<!-- div:right-panel -->

[EquipmentRequirement.java](https://raw.githubusercontent.com/art-framework/art-framework/master/bukkit/src/main/java/io/artframework/bukkit/requirements/EquipmentRequirement.java ':include :fragment=demo')

<!-- panels:end -->