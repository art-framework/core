package io.artframework.impl;

import io.artframework.ConfigMap;
import io.artframework.ExecutionContext;
import io.artframework.ResolveContext;
import io.artframework.Scope;
import io.artframework.Target;
import io.artframework.conf.KeyValuePair;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Value
@Accessors(fluent = true)
public class DefaultResolveContext implements ResolveContext {

    @NonNull Scope scope;
    @NonNull ConfigMap configMap;
    @NonNull Class<?> type;
    @NonNull List<KeyValuePair> configValues;
    @Nullable Target<?> target;
    @Nullable ExecutionContext<?> executionContext;

    public Optional<Target<?>> target() {

        return Optional.ofNullable(target);
    }

    public Optional<ExecutionContext<?>> executionContext() {

        return Optional.ofNullable(executionContext);
    }
}
