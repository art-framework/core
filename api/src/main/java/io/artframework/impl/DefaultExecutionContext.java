/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.artframework.impl;

import com.google.common.collect.ImmutableList;
import io.artframework.*;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.*;

@Accessors(fluent = true)
public class DefaultExecutionContext<TContext extends ArtObjectContext<?>> extends AbstractScoped implements ExecutionContext<TContext> {

    private final Context root;
    private final Container container;
    private final TContext currentContext;
    private final Map<String, Variable<?>> variables = new HashMap<>();

    public DefaultExecutionContext(
            @NonNull Scope scope,
            @Nullable Context root,
            @NonNull Target<?>... targets
    ) {
        super(scope);
        this.root = root;
        this.container = new Container(targets);
        this.currentContext = null;
    }

    DefaultExecutionContext(Scope scope, Context root, Container container, TContext currentContext) {
        super(scope);
        this.root = root;
        this.container = container;
        this.currentContext = currentContext;
    }

    @Override
    public Optional<Context> root() {
        return Optional.ofNullable(root);
    }

    public Map<String, Variable<?>> variables() {

        return root().map(Context::variables).orElse(variables);
    }

    @Override
    public Optional<ArtObjectContext<?>> parent() {
        if (container.history.empty()) {
            return Optional.empty();
        }
        return Optional.of(container.history.peek());
    }

    @Override
    public Collection<ArtObjectContext<?>> history() {
        return ImmutableList.copyOf(container.history);
    }

    @Override
    public Collection<Target<?>> targets() {
        return ImmutableList.copyOf(container.targets);
    }

    @Override
    public <TTarget> ExecutionContext<TContext> addTarget(Target<TTarget> target) {
        container.targets.add(target);
        return this;
    }

    @Override
    public TContext current() {
        return currentContext;
    }

    @Override
    public <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value) {
        if (current() != null) {
            return current().store(target, key, value);
        }
        return Optional.empty();
    }

    @Override
    public <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass) {
        if (current() != null) {
            return current().store(target, key, valueClass);
        }
        return Optional.empty();
    }

    @Override
    public @NonNull Map<String, Object> data() {
        return container.data;
    }

    @Override
    public <TNextContext extends ArtObjectContext<TArtObject>, TArtObject extends ArtObject> ExecutionContext<TNextContext> next(TNextContext nextContext) {
        if (current() != null) container.history.push(current());
        return new DefaultExecutionContext<>(scope(), root, container, nextContext);
    }

    private static class Container {

        private final Map<String, Object> data;
        private final Stack<ArtObjectContext<?>> history;
        private final Set<Target<?>> targets;

        private Container() {
            this.data = new HashMap<>();
            this.history = new Stack<>();
            this.targets = new HashSet<>();
        }

        private Container(Target<?>... targets) {
            this.data = new HashMap<>();
            this.history = new Stack<>();
            this.targets = new HashSet<>(Arrays.asList(targets));
        }
    }
}
