By default the art-framework scans the classpath and registers all [actions](actions.md), [requirements](requirements.md), [trigger](trigger.md), [targets](targets.md), [resolvers](resolver.md) and [replacements](variables.md) it can find.

!> This is only possible for classes that have a public parameterless constructor.

For all other cases (except targets) you need to register your class manually in the [onLoad](modules.md#onload) method of the [scope](scope.md) lifecycle. Targets are a special case and require the target type as single constructor parameter.

```java
@ArtModule("my-module")
public class MyModule {

    @OnLoad
    public void onLoad(Scope scope) {

        scope.register()
            .actions().add(MyAction.class, () -> ...)
            .and()
            .requirements().add(MyRequirement.class, () -> ...)
            .and()
            .trigger().add(MyTrigger.class, () -> ...)
            .and()
            .targets().add(MyTargetSource.class, (source) -> ...)
            .and()
            .resolvers().add(MyResolver.class, () -> ...)
            .and()
            .replacements().add(new MyReplacement(...));
    }
}
```

> [!NOTE]
> All art-objects listed above, except replacements, are created multiple times during the lifecycle of an [ArtContext](art-context.md).  
> Make sure they have no initialization logic in the constructor. Use the [`Configurable`](config.md) interface for that.
