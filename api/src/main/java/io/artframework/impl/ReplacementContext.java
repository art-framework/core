package io.artframework.impl;

import io.artframework.ExecutionContext;
import io.artframework.Scope;
import io.artframework.Target;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.Optional;

@Value
@Accessors(fluent = true)
public class ReplacementContext {

    @NonNull Scope scope;
    @Nullable Target<?> target;
    @Nullable ExecutionContext<?> executionContext;

    public Optional<Target<?>> target() {

        return Optional.ofNullable(target);
    }

    public Optional<ExecutionContext<?>> executionContext() {

        return Optional.ofNullable(executionContext);
    }
}
