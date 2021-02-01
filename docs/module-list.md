As said in the [introduction](introduction.md) the art-framework is [highly modular](developer/modules.md) and you add new modules to it anytime. Here is a list of modules or plugins providing modules for the art-framework.

This list is by no means complete and if you want your plugin or module to be included here, [create a pull request](https://github.com/art-framework/art-framework/edit/master/docs/module-list.md) or contact me.

> [!NOTE]
> To install a module download it and drop it into the `plugins/art-framework/modules/` directory.  
> See the [installation documentation](installation.md) for more details.

## [PlaceholderAPI](https://github.com/art-framework/art-placeholderapi)

[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/art-framework/art-placeholderapi?include_prereleases&label=release)](https://github.com/art-framework/art-placeholderapi/releases)

Replaces placeholders in any art-framework parsed config.

```yaml
art:
  - '@player.join'
  - '!broadcast "%player_name% &ajoined the server! They are rank &f%vault_rank%"'
```
