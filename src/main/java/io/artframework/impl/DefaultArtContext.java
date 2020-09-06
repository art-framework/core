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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.artframework.util.ReflectionUtil.getEntryForTarget;

public class DefaultArtContext extends AbstractScoped implements ArtContext, TriggerListener<Object> {

    private final ArtSettings settings;

    @Getter
    private final List<ArtObjectContext<?>> artContexts;
    private final Map<Class<?>, List<TriggerListener<?>>> triggerListeners = new HashMap<>();
    private final Map<String, Object> data = new HashMap<>();

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
                getArtContexts()
        );
    }

    @Override
    public FutureResult execute(@NonNull Target<?>... targets) {

        return execute(ExecutionContext.of(scope(), this, targets));
    }

    @SuppressWarnings("unchecked")
    private FutureResult execute(ExecutionContext<?> executionContext) {
        return executeContext(ActionContext.class, actionContext ->
                actionContext.execute(executionContext.next(actionContext)),
                getArtContexts()
        ).future();
    }

    @SuppressWarnings("unchecked")
    private <TTarget> void execute(Target<TTarget> target, ExecutionContext<?> context) {
        executeContext(ActionContext.class, actionContext ->
                        actionContext.execute(target, context.next(actionContext)),
                getArtContexts()
        );
    }

    @Override
    public <TTarget> void onTrigger(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
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
                if (settings.executeActions()) execute(target, context);

                successfulTargets.add(target);
            }
        }

        callListeners(successfulTargets, context);

    }

    private void callListeners(List<Target<?>> targets, ExecutionContext<TriggerContext> context) {

        targets.stream()
                .collect(Collectors.groupingBy(target -> target.source().getClass()))
                .forEach((key, value) -> callTargetListeners(toTargetCollection(key, value), context));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> List<Target<TTarget>> toTargetCollection(Class<TTarget> targetClass, Collection<Target<?>> targets) {
        return targets.stream()
                .filter(target -> target.isTargetType(targetClass))
                .map(target -> (Target<TTarget>) target)
                .collect(Collectors.toList());
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
        disableTrigger();
    }

    @Override
    public ArtContext enableTrigger() {
        getArtContexts().stream()
                .filter(artObjectContext -> artObjectContext instanceof TriggerContext)
                .map(artObjectContext -> (TriggerContext) artObjectContext)
                .forEach(context -> context.addListener(this));
        return this;
    }

    @Override
    public ArtContext disableTrigger() {
        getArtContexts().stream()
                .filter(artObjectContext -> artObjectContext instanceof TriggerContext)
                .map(artObjectContext -> (TriggerContext) artObjectContext)
                .forEach(context -> context.removeListener(this));
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
