You only need to depend on the `io.art-framework.core:api` dependency using the [jitpack repository](https://jitpack.io/).

Take a look at the [developer quickstart guide](/developer) to get started with the `art-framework`.

!> Do not shade the art-framework into your plugin.

## Gradle

[gradle](../gradle.md ':include')

## Maven

[maven](../maven.md ':include')

## Plugin Dependency

If you are using the [bukkit-platform](platforms/bukkit.md) of the art-framework and directly included it in your plugin you can set the `softdepend` property in your `plugin.yml` to make the art-framwork load before your plugin.

Your plugin will still work even without the art-framework.

**Example Softdepend:**

```yaml
name: ExamplePlugin
version: 1.0
author: author
main: your.main.path.here

softdepend: [art-framework] # This is used to load the art-framework before your plugin loads
```

Then go to the **[Developer Quickstart Guide](/developer/README.md)** to learn how you can create [actions](developer/actions.md), [requirements](developer/requirements.md) and [trigger](developer/trigger.md).

## Badge

You are using the art-framework in your plugin? Use the following badge to show how cool you are!

[![art-framework-badge](https://raw.githubusercontent.com/gist/Silthus/a88fd35b722da343658d54c474c0e5c1/raw/12e79cf109e675cfe6999dd08d1a00c00c46b440/badge.svg)](https://art-framework.io)

```markdown
[![art-framework-badge](https://raw.githubusercontent.com/gist/Silthus/a88fd35b722da343658d54c474c0e5c1/raw/12e79cf109e675cfe6999dd08d1a00c00c46b440/badge.svg)](https://art-framework.io)
```
