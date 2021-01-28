The three primary annotations you need to remember are the `@ART`, `@ArtModule` and `@ConfigOption` annotation. The [javadocs](https://jdocs.art-framework.io/io/artframework/annotations/package-summary.html) explains the usage of all annotations pretty well, but here is a summary for you to look up.

## @ART

Every [action](actions.md), [requirement](requirements.md) and [trigger](trigger.md) must be annotated with the `@ART` annotation to provide a unique identifier and additional meta data about the art-object.

> All of the following metadata is optional, except the value (identifier).  
> However it is highly recommended to provide as much information as possible about your art-object. The data might be used for a public registry of art-modules in the future.

| Setting | Default | Description |
| ------- | ------- | ----------- | 
| `value` |         | The required unique identifier of the art-object. It is recommended to prepend the name of the plugin separated by a colon and then followed by the art-object identifier. |
| `alias` | `[]`    | A list of aliases for your art-object. They can be used instead of the identifier, but do not fail to register your art-object if an alias already exists. |
| `description` | `[]` | An optional multiline description of your art-object. Tell the user what it does and how and when to use it. |
| `autoRegister` | `true` | Set the value to false to prevent the art-framework from auto registering your art-object if it is loaded by a class path scanner. |
