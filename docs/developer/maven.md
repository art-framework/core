# Maven Dependency

You only need to depend on the `net.silthus.art:art-core` package or the corresponding implementation, e.g. `net.silthus.art:art-bukkit`.

!!! important
    Do not shade the ART-Framework into your plugin.

## Gradle

```gradle
dependencies {
    implementation group: 'net.silthus.art', name: 'art-bukkit', version: '1.0.0-alpha.8'
}
```

## Maven

```xml
<project>
  ...
  <dependencies>
    <dependency>
      <groupId>net.silthus.art</groupId>
      <artifactId>art-core</artifactId>
      <version>1.0.0-alpha.8</version>
      <scope>provided</scrope>
    </dependency>
  </dependencies>
  ...
</project>
```
