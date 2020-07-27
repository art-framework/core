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
import com.google.inject.assistedinject.Assisted;
import io.artframework.*;
import io.artframework.conf.ArtSettings;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.function.Function;

import static io.artframework.util.ReflectionUtil.getEntryForTarget;

public class DefaultArtContext extends AbstractScope implements ArtContext, TriggerListener<Object> {

    private final ArtSettings settings;

    @Getter
    private final List<ArtObjectContext<?>> artContexts;
    private final Map<Class<?>, List<TriggerListener<?>>> triggerListeners = new HashMap<>();
    private final Map<String, Object> data = new HashMap<>();

    public DefaultArtContext(Configuration configuration, ArtSettings settings, @Assisted Collection<ArtObjectContext<?>> artContexts) {
        super(configuration);
        this.settings = settings;
        this.artContexts = ImmutableList.copyOf(artContexts);

        registerListeners();
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
        return settings().isAutoTrigger() || triggerListeners.size() > 0;
    }

    @Override
    public <TTarget> CombinedResult test(@NonNull Target<TTarget> target) {

        return test(ExecutionContext.of(configuration(), this, target));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> CombinedResult test(Target<TTarget> target, ExecutionContext<?> executionContext) {

        return executeContext(target, RequirementContext.class, requirementContext ->
                requirementContext.test(target, executionContext.next(requirementContext)),
                getArtContexts()
        );
    }

    @Override
    public <TTarget> FutureResult execute(@NonNull Target<TTarget> target) {

        return execute(target, ExecutionContext.of(configuration(), this, target));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> FutureResult execute(Target<TTarget> target, ExecutionContext<?> executionContext) {
        return executeContext(target, ActionContext.class, actionContext ->
                actionContext.execute(target, executionContext.next(actionContext)),
                getArtContexts()
        ).future();
    }

    @Override
    public <TTarget> void registerListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
        if (!triggerListeners.containsKey(targetClass)) {
            triggerListeners.put(targetClass, new ArrayList<>());
        }
        triggerListeners.get(targetClass).add(listener);
    }

    @Override
    public void onTrigger(Target<Object>[] targets, ExecutionContext<TriggerContext> context) {
        if (!isAutoTrigger()) return;

        List<Target<?>> successfulTargets = new ArrayList<>();

        for (Target<Object> target : targets) {
            if (test(target, context).success()) {
                if (settings.isExecuteActions()) execute(target, context);

                successfulTargets.add(target);
            }
        }

        callListeners(successfulTargets, context);

    }

    @SuppressWarnings("unchecked")
    private void callListeners(List<Target<?>> targets, ExecutionContext<TriggerContext> context) {

        // TODO: gather same targets and callTargetListeners
    }

    @SuppressWarnings("unchecked")
    private <TTarget> void callTargetListeners(List<Target<TTarget>> targets, ExecutionContext<TriggerContext> context) {

        if (targets.isEmpty()) return;

        getEntryForTarget(targets.get(0), triggerListeners)
                .orElse(new ArrayList<>())
                .stream()
                .map(listener -> (TriggerListener<TTarget>) listener)
                .forEach(triggerListener -> triggerListener.onTrigger(targets.toArray(new Target[0]), context));
    }

    @Override
    public ArtContext combine(ArtContext context) {
        return new CombinedArtContext(this, context);
    }

    @Override
    public void close() {
        unregisterListeners();
    }

    private void registerListeners() {
        getArtContexts().stream()
                .filter(artObjectContext -> artObjectContext instanceof TriggerContext)
                .map(artObjectContext -> (TriggerContext) artObjectContext)
                .forEach(context -> context.addListener(this));
    }

    private void unregisterListeners() {
        getArtContexts().stream()
                .filter(artObjectContext -> artObjectContext instanceof TriggerContext)
                .map(artObjectContext -> (TriggerContext) artObjectContext)
                .forEach(context -> context.removeListener(this));
    }

    @SuppressWarnings("unchecked")
    protected final <TTarget, TContext> CombinedResult executeContext(
            Target<TTarget> target,
            Class<TContext> contextClass,
            Function<TContext, Result> function,
            Collection<ArtObjectContext<?>> contexts)
    {
        return contexts.stream()
                .filter(contextClass::isInstance)
                .filter(artObjectContext -> artObjectContext.isTargetType(target))
                .map(artObjectContext -> (TContext) artObjectContext)
                .map(function)
                .map(CombinedResult::of)
                .reduce(CombinedResult::combine)
                .orElse(CombinedResult.of(empty()));
    }
}
