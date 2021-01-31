There are several utility classes that make developing art easier.

## ModifierMatcher

The `ModifierMatcher` takes an input pattern in the form of `>=5`, `<3` or `2` and matches it against the provided value. Look at the [javadocs](https://jdocs.art-framework.io/io/artframework/util/ModifierMatcher.html) for more details on the class.

```java
ModifierMatcher matcher = new ModifierMatcher(">5");
if (matcher.matches(10)) {
    ...
}
```
