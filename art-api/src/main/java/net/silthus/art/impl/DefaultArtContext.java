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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.api.parser.Filter;
import net.silthus.art.api.requirements.RequirementWrapper;
import net.silthus.art.api.trigger.TriggerListener;
import net.silthus.art.conf.ArtContextSettings;

import java.util.*;
import java.util.stream.Collectors;

import static net.silthus.art.util.ReflectionUtil.getEntryForTarget;

public final class DefaultArtContext extends AbstractScope implements ArtContext, TriggerListener<Object> {

    private final ArtContextSettings settings;

    @Getter
    private final List<ArtObjectContext> art;
    private final Map<Class<?>, List<TriggerListener<?>>> triggerListeners = new HashMap<>();

    @Inject
    public DefaultArtContext(Configuration configuration, ArtContextSettings settings, @Assisted Collection<ArtObjectContext> art) {
        super(configuration);
        this.settings = settings;
        this.art = ImmutableList.copyOf(art);
    }

    @Override
    public ArtContextSettings settings() {
        return settings;
    }

    private boolean isAutoTrigger() {
        return settings().isAutoTrigger() || triggerListeners.size() > 0;
    }

    @Override
    public final <TTarget> boolean test(Target<TTarget> target) {

        return test(target, new ArrayList<Filter<TTarget>>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> boolean test(Target<TTarget> target, Collection<Filter<TTarget>> filters) {

        if (Objects.isNull(target)) return false;

        return testFilter(target, filters) && testGlobalFilter(target) && getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof RequirementWrapper)
                .map(artContext -> (RequirementWrapper<TTarget, ?>) artContext)
                .allMatch(requirement -> requirement.test(target));
    }

    @Override
    public final <TTarget> void execute(Target<TTarget> target) {

        execute(target, new ArrayList<Filter<TTarget>>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> void execute(Target<TTarget> target, Collection<Filter<TTarget>> filters) {

        if (Objects.isNull(target)) return;
        if (!testFilter(target, filters)) return;
        if (!testGlobalFilter(target)) return;

        List<ActionContext<TTarget>> actions = getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof DefaultActionContext)
                .map(artContext -> (ActionContext<TTarget>) artContext)
                .collect(Collectors.toList());

        ExecutionContext<?> context = ExecutionContext.of(configuration(), this);
        for (ActionContext<TTarget> action : actions) {
            context = action.execute(target, context);
        }
    }

    @Override
    public <TTarget> void onTrigger(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
        if (!triggerListeners.containsKey(targetClass)) {
            triggerListeners.put(targetClass, new ArrayList<>());
        }
        triggerListeners.get(targetClass).add(listener);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void onTrigger(@NonNull Target target) {
        if (isAutoTrigger() && test(target)) {
            if (settings().isExecuteActions()) execute(target);

            getEntryForTarget(target.getSource(), triggerListeners)
                    .orElse(new ArrayList<>())
                    .forEach(triggerListener -> triggerListener.onTrigger(target));
        }
    }

    private <TTarget> boolean testFilter(Target<TTarget> target, Collection<Filter<TTarget>> filters) {
        return filters.stream().allMatch(filter -> filter.test(target));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> boolean testGlobalFilter(Target<TTarget> target) {

        if (Objects.isNull(target)) return false;

        return getEntryForTarget(target, ).orElse(new ArrayList<>()).stream()
                .map(filter -> (Filter<TTarget>) filter)
                .allMatch(filter -> filter.test(target, config));
    }
}
