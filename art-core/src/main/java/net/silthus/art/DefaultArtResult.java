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

package net.silthus.art;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.Filter;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.art.api.target.Target;
import net.silthus.art.api.trigger.TriggerListener;

import java.util.*;

import static net.silthus.art.util.ReflectionUtil.getEntryForTarget;

public final class DefaultArtResult implements ArtResult, TriggerListener<Object> {

    static DefaultArtResult empty() {
        return new DefaultArtResult(new ArtConfig(), new ArrayList<>(), new HashMap<>());
    }

    private final ArtConfig config;
    @Getter(AccessLevel.PACKAGE)
    private final List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> art;
    @Getter(AccessLevel.PACKAGE)
    private final Map<Class<?>, List<Filter<?>>> filters;
    private final Map<Class<?>, List<TriggerListener<?>>> triggerListeners = new HashMap<>();

    @Setter
    private boolean autoTrigger = true;
    @Getter
    @Setter
    private boolean executeActions = true;

    @Inject
    public DefaultArtResult(@Assisted ArtConfig config, @Assisted Collection<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> art, @Assisted Map<Class<?>, List<Filter<?>>> filters) {
        this.config = config;
        this.art = ImmutableList.copyOf(art);
        this.filters = ImmutableMap.copyOf(filters);
    }

    public boolean isAutoTrigger() {
        return autoTrigger || triggerListeners.size() > 0;
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
                .filter(artContext -> artContext instanceof RequirementContext)
                .map(artContext -> (RequirementContext<TTarget, ?>) artContext)
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

        getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof ActionContext)
                .map(artContext -> (ActionContext<TTarget, ?>) artContext)
                .forEach(action -> action.execute(target));
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
            if (isExecuteActions()) execute(target);

            getEntryForTarget(target.getSource(), triggerListeners)
                    .orElse(new ArrayList<>())
                    .forEach(triggerListener -> triggerListener.onTrigger(target));
        }
    }

    private <TTarget> boolean testFilter(Target<TTarget> target, Collection<Filter<TTarget>> filters) {
        return filters.stream().allMatch(filter -> filter.test(target, config));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> boolean testGlobalFilter(Target<TTarget> target) {

        if (Objects.isNull(target)) return false;

        return getEntryForTarget(target, getFilters()).orElse(new ArrayList<>()).stream()
                .map(filter -> (Filter<TTarget>) filter)
                .allMatch(filter -> filter.test(target, config));
    }
}
