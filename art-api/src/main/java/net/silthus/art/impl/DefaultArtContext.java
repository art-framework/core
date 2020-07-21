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
import net.silthus.art.conf.ArtSettings;

import java.util.*;

import static net.silthus.art.util.ReflectionUtil.getEntryForTarget;

public class DefaultArtContext extends AbstractScope implements ART, TriggerListener<Object> {

    private final ArtSettings settings;

    @Getter
    private final List<ArtObjectContext<?>> artContexts;
    private final Map<Class<?>, List<TriggerListener<?>>> triggerListeners = new HashMap<>();
    private final Map<String, Object> data = new HashMap<>();

    @Inject
    public DefaultArtContext(Configuration configuration, ArtSettings settings, @Assisted Collection<ArtObjectContext<?>> artContexts) {
        super(configuration);
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
        return settings().isAutoTrigger() || triggerListeners.size() > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> boolean test(@NonNull Target<TTarget> target) {

        ExecutionContext<TTarget, ?> executionContext = ExecutionContext.of(getConfiguration(), this, target);

        return getArtContexts().stream()
                .filter(context -> context.isTargetType(target))
                .filter(requirement -> requirement instanceof RequirementContext)
                .map(requirement -> (RequirementContext<TTarget>) requirement)
                .allMatch(executionContext::test);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> void execute(@NonNull Target<TTarget> target) {

        ExecutionContext<TTarget, ?> executionContext = ExecutionContext.of(getConfiguration(), this, target);

        getArtContexts().stream()
                .filter(context -> context.isTargetType(target))
                .filter(action -> action instanceof ActionContext)
                .map(action -> (ActionContext<TTarget>) action)
                .forEach(executionContext::execute);
    }

    @Override
    public <TTarget> void registerListener(Class<TTarget> targetClass, TriggerListener<TTarget> listener) {
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

    @Override
    public ART combine(ART context) {
        return new CombinedArtContext(this, context);
    }
}
