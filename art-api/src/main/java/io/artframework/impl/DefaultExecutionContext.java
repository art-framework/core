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

import io.artframework.*;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class DefaultExecutionContext<TContext extends ArtObjectContext<?>> extends AbstractScope implements ExecutionContext<TContext> {

    private final Context root;
    @Getter
    private final Target<?>[] targets;
    private final Container container;
    private final TContext currentContext;

    public DefaultExecutionContext(
            @NonNull Configuration configuration,
            @Nullable Context root,
            @NonNull Target<?>... targets
    ) {
        super(configuration);
        this.root = root;
        this.targets = targets;
        this.container = new Container();
        this.currentContext = null;
    }

    DefaultExecutionContext(Configuration configuration, Context root, Target<?>[] targets, Container container, TContext currentContext) {
        super(configuration);
        this.root = root;
        this.targets = targets;
        this.container = container;
        this.currentContext = currentContext;
    }

    @Override
    public Optional<Context> root() {
        return Optional.ofNullable(root);
    }

    @Override
    public Optional<ArtObjectContext<?>> parent() {
        if (container.history.empty()) {
            return Optional.empty();
        }
        return Optional.of(container.history.peek());
    }

    @Override
    public ArtObjectContext<?>[] history() {
        return container.history.toArray(new ArtObjectContext[0]);
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
        return new DefaultExecutionContext<>(getConfiguration(), root, targets, container, nextContext);
    }

    static class Container {

        private final Map<String, Object> data;
        private final Stack<ArtObjectContext<?>> history;

        Container() {
            this.data = new HashMap<>();
            this.history = new Stack<>();
        }
    }
}
