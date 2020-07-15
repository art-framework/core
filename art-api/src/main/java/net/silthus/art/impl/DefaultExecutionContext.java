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

package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class DefaultExecutionContext<TTarget, TContext extends ArtObjectContext> extends AbstractScope implements ExecutionContext<TTarget, TContext> {

    private final Context rootContext;
    private final Target<TTarget> target;
    private final Container container;
    private final TContext currentContext;

    public DefaultExecutionContext(
            @NonNull Configuration configuration,
            @Nullable Context rootContext,
            @NonNull Target<TTarget> target
    ) {
        super(configuration);
        this.rootContext = rootContext;
        this.target = target;
        this.container = new Container();
        this.currentContext = null;
    }

    DefaultExecutionContext(Configuration configuration, Context rootContext, Target<TTarget> target, Container container, TContext currentContext) {
        super(configuration);
        this.rootContext = rootContext;
        this.target = target;
        this.container = container;
        this.currentContext = currentContext;
    }

    @Override
    public Optional<Context> root() {
        return Optional.ofNullable(rootContext);
    }

    @Override
    public Optional<ArtObjectContext> parent() {
        if (container.history.empty()) {
            return Optional.empty();
        }
        return Optional.of(container.history.peek());
    }

    @Override
    public ArtObjectContext[] history() {
        return container.history.toArray(new ArtObjectContext[0]);
    }

    @Override
    public TContext current() {
        return currentContext;
    }

    @Override
    public Target<TTarget> target() {
        return target;
    }

    @Override
    public <TValue> Optional<TValue> store(@NonNull String key, @NonNull TValue value) {
        if (current() != null) {
            return current().store(target(), key, value);
        }
        return Optional.empty();
    }

    @Override
    public <TValue> Optional<TValue> store(@NonNull String key, @NonNull Class<TValue> valueClass) {
        if (current() != null) {
            return current().store(target(), key, valueClass);
        }
        return Optional.empty();
    }

    @Override
    public @NonNull Map<String, Object> data() {
        return container.data;
    }

    @Override
    public <TNextContext extends ArtObjectContext> ExecutionContext<TTarget, TNextContext> next(TNextContext nextContext) {
        if (current() != null) container.history.push(current());
        return new DefaultExecutionContext<>(configuration(), rootContext, target, container, nextContext);
    }

    @Override
    public <TNextContext extends ActionContext<TTarget>> void execute(TNextContext nextContext) {
        execute(nextContext, nextContext);
    }

    @Override
    public <TNextContext extends ActionContext<TTarget>> void execute(TNextContext nextContext, Action<TTarget> action) {
        action.execute(next(nextContext));
    }

    @Override
    public <TNextContext extends RequirementContext<TTarget>> boolean test(TNextContext nextContext) {
        return test(nextContext, nextContext);
    }

    @Override
    public <TNextContext extends RequirementContext<TTarget>> boolean test(TNextContext nextContext, Requirement<TTarget> requirement) {
        return requirement.test(next(nextContext));
    }

    static class Container {

        private final Map<String, Object> data;
        private final Stack<ArtObjectContext> history;

        Container() {
            this.data = new HashMap<>();
            this.history = new Stack<>();
        }
    }
}
