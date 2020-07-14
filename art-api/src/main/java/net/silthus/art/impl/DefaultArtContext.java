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
import net.silthus.art.api.trigger.TriggerListener;
import net.silthus.art.conf.ArtContextSettings;

import java.util.*;

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
    @SuppressWarnings("unchecked")
    public final <TTarget> boolean test(@NonNull Target<TTarget> target) {

        ExecutionContext<TTarget, ArtObjectContext> executionContext = ExecutionContext.of(configuration(), this, target);

        return getArt().stream()
                .filter(context -> context.isTargetType(target))
                .filter(requirement -> requirement instanceof RequirementContext)
                .map(requirement -> (RequirementContext<TTarget>) requirement)
                .allMatch(executionContext::test);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> void execute(@NonNull Target<TTarget> target) {

        ExecutionContext<TTarget, ArtObjectContext> executionContext = ExecutionContext.of(configuration(), this, target);

        getArt().stream()
                .filter(context -> context.isTargetType(target))
                .filter(action -> action instanceof ActionContext)
                .map(action -> (ActionContext<TTarget>) action)
                .forEach(executionContext::execute);
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
}
