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
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class DefaultArtResult implements ArtResult {

    private final ArtConfig config;
    @Getter(AccessLevel.PACKAGE)
    private final List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> art;
    @Getter(AccessLevel.PACKAGE)
    private final Map<Class<?>, List<ArtResultFilter<?>>> globalFilters;
    @Getter(AccessLevel.PACKAGE)
    private final List<Map.Entry<Class<?>, BiPredicate<?, ArtConfig>>> additionalFilters = new ArrayList<>();

    @Inject
    public DefaultArtResult(@Assisted ArtConfig config, @Assisted List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> art, @Assisted Map<Class<?>, List<ArtResultFilter<?>>> globalFilters) {
        this.config = config;
        this.art = ImmutableList.copyOf(art);
        this.globalFilters = globalFilters;
    }

    @Override
    public <TTarget> void addFilter(Class<TTarget> targetClass, ArtResultFilter<TTarget> predicate) {
        this.additionalFilters.add(Map.entry(targetClass, predicate));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> boolean test(TTarget target) {

        boolean allRequirementsMatch = getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof RequirementContext)
                .map(artContext -> (RequirementContext<TTarget, ?>) artContext)
                .allMatch(requirement -> requirement.test(target));

        return allRequirementsMatch
                && isMatchingGlobalFilters(target)
                && isMatchingAdditionalFilters(target);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> void execute(TTarget target) {

        if (!isMatchingAdditionalFilters(target)) return;
        if (!isMatchingGlobalFilters(target)) return;

        getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof ActionContext)
                .map(artContext -> (ActionContext<TTarget, ?>) artContext)
                .forEach(action -> action.execute(target));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> boolean isMatchingGlobalFilters(TTarget target) {
        return getGlobalFilters().entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(target))
                .flatMap(entry -> entry.getValue().stream())
                .map(filter -> (ArtResultFilter<TTarget>) filter)
                .allMatch(filter -> filter.test(target, config));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> boolean isMatchingAdditionalFilters(TTarget target) {
        return getAdditionalFilters().stream()
                .filter(entry -> entry.getKey().isInstance(target))
                .map(entry -> (ArtResultFilter<TTarget>) entry.getValue())
                .allMatch(predicate -> predicate.test(target, config));
    }
}
