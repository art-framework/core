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
import io.artframework.conf.ArtSettings;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

import java.util.*;
import java.util.function.Function;

import static io.artframework.util.ReflectionUtil.getEntryForTarget;

@Accessors(fluent = true)
@Log(topic = "art-framework")
public class DefaultArtContext extends AbstractScoped implements ArtContext, TriggerListener<Object> {

    private final ArtSettings settings;

    @Getter
    private final List<ArtObjectContext<?>> artContexts;
    private final Map<Class<?>, List<TriggerListener<?>>> triggerListeners = new HashMap<>();
    private final Map<String, Object> data = new HashMap<>();
    @Getter
    private final Map<String, Variable<?>> variables = new HashMap<>();

    public DefaultArtContext(Scope scope, ArtSettings settings, Collection<ArtObjectContext<?>> artContexts) {
        super(scope);
        this.settings = settings;
        this.artContexts = ImmutableList.copyOf(artContexts);
    }

    @Override
    public ArtSettings settings() {
        return settings;
    }

    @Override
    public @NonNull Map<String, Object> data() {
        return data;
    }

    private boolean isAutoTrigger() {
        return settings().autoTrigger() || triggerListeners.size() > 0;
    }

    @Override
    public <TTarget> CombinedResult test(@NonNull Target<TTarget> target) {

        return test(target, ExecutionContext.of(scope(), this, target));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> CombinedResult test(Target<TTarget> target, ExecutionContext<?> executionContext) {

        return executeContext(RequirementContext.class, requirementContext ->
                requirementContext.test(target, executionContext.next(requirementContext)),
                artContexts()
        );
    }

    @Override
    public FutureResult execute(@NonNull Target<?>... targets) {

        if (targets.length < 1) {
            log.warning("skipping execution of art-context with 0 targets");
            return FutureResult.empty();
        }

        return execute(ExecutionContext.of(scope(), this, targets));
    }

    @SuppressWarnings("unchecked")
    private FutureResult execute(ExecutionContext<?> executionContext) {
        return executeContext(ActionContext.class, actionContext ->
                actionContext.execute(executionContext.next(actionContext)),
                artContexts()
        ).future();
    }

    @SuppressWarnings("unchecked")
    private <TTarget> void execute(Target<TTarget> target, ExecutionContext<?> context) {
        executeContext(ActionContext.class, actionContext ->
                        actionContext.execute(target, context.next(actionContext)),
                artContexts()
        );
    }

    @Override
    public <TTarget> ArtContext onTrigger(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {

        if (!triggerListeners.containsKey(targetClass)) {
            triggerListeners.put(targetClass, new ArrayList<>());
        }

        triggerListeners.get(targetClass).add(listener);

        return this;
    }

    @Override
    public void onTrigger(Target<Object> target, ExecutionContext<TriggerContext> context) {

        if (!isAutoTrigger()) return;

        if (test(target, context).success()) {
            if (settings.executeActions()) execute(target, context);

            callListeners(target, context);
        }
    }

    @SuppressWarnings("unchecked")
    private <TTarget> void callListeners(Target<TTarget> target, ExecutionContext<TriggerContext> context) {

        if (target == null) return;

        getEntryForTarget(target, triggerListeners).stream()
                .flatMap(Collection::stream)
                .map(listener -> (TriggerListener<TTarget>) listener)
                .forEach(triggerListener -> triggerListener.onTrigger(target, context));
    }

    @Override
    public ArtContext combine(ArtContext context) {
        return new CombinedArtContext(this, context);
    }

    @Override
    public ArtContext enableTrigger() {
        artContexts().stream()
                .filter(artObjectContext -> artObjectContext instanceof TriggerContext)
                .map(artObjectContext -> (TriggerContext) artObjectContext)
                .forEach(context -> context.addListener(this).enable());
        return this;
    }

    @Override
    public ArtContext disableTrigger() {
        artContexts().stream()
                .filter(artObjectContext -> artObjectContext instanceof TriggerContext)
                .map(artObjectContext -> (TriggerContext) artObjectContext)
                .forEach(context -> context.removeListener(this).disable());
        return this;
    }

    @SuppressWarnings("unchecked")
    protected final <TContext> CombinedResult executeContext(
            Class<TContext> contextClass,
            Function<TContext, Result> function,
            Collection<ArtObjectContext<?>> contexts)
    {
        return contexts.stream()
                .filter(contextClass::isInstance)
                .map(artObjectContext -> (TContext) artObjectContext)
                .map(function)
                .map(CombinedResult::of)
                .reduce(CombinedResult::combine)
                .orElse(CombinedResult.of(empty()));
    }
}
