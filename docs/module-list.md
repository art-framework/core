As said in the [introduction](introduction.md) the art-framework is [highly modular](developer/modules.md) and you add new modules to it anytime. Here is a list of modules or plugins providing modules for the art-framework.

This list is by no means complete and if you want your plugin or module to be included here, [create a pull request](https://github.com/art-framework/art-framework/edit/master/docs/module-list.md) or contact me.

## Installing a module

To install a module download it and drop it into the `plugins/art-framework/modules/` directory. Then restart your server. *A hot reload command is coming soon.*

## Common Modules

Common modules are modules that have no external dependencies. Meaning they function just with the bare art-framework and implementing platform. Examples for this are the `timer` and `tag` module.

## Plugin Modules

Plugin modules are modules that require a plugin to work and add functionality from it. Examples for this would be the `art-placeholderapi` module that adds the [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) replacements to the art-framework.

### [art-placeholderapi](https://github.com/art-framework/art-placeholderapi)

Replaces placeholders in any art-framework parsed config.

```yaml
art:
  - '@player.join'
  - '!broadcast "%player_name% &ajoined the server! They are rank &f%vault_rank%"'
```
