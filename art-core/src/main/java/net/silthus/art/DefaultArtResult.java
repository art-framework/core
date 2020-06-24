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
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.config.ArtObjectConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.art.api.parser.ArtResultFilter;

import java.util.*;

public final class DefaultArtResult implements ArtResult {

    static ArtResult empty() {
        return new DefaultArtResult(new ArtConfig(), new ArrayList<>(), new HashMap<>());
    }

    private final ArtConfig config;
    @Getter(AccessLevel.PACKAGE)
    private final List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> art;
    @Getter(AccessLevel.PACKAGE)
    private final Map<Class<?>, List<ArtResultFilter<?>>> filters;

    @Inject
    public DefaultArtResult(@Assisted ArtConfig config, @Assisted List<ArtContext<?, ?, ? extends ArtObjectConfig<?>>> art, @Assisted Map<Class<?>, List<ArtResultFilter<?>>> filters) {
        this.config = config;
        this.art = ImmutableList.copyOf(art);
        this.filters = ImmutableMap.copyOf(filters);
    }

    @Override
    public final <TTarget> boolean test(TTarget target) {

        return test(target, new ArrayList<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> boolean test(TTarget target, Collection<ArtResultFilter<TTarget>> filters) {

        if (Objects.isNull(target)) return false;

        return testFilter(target, filters) && testGlobalFilter(target) && getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof RequirementContext)
                .map(artContext -> (RequirementContext<TTarget, ?>) artContext)
                .allMatch(requirement -> requirement.test(target));
    }

    @Override
    public final <TTarget> void execute(TTarget target) {

        execute(target, new ArrayList<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> void execute(TTarget target, Collection<ArtResultFilter<TTarget>> filters) {

        if (Objects.isNull(target)) return;
        if (!testFilter(target, filters)) return;
        if (!testGlobalFilter(target)) return;

        getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof ActionContext)
                .map(artContext -> (ActionContext<TTarget, ?>) artContext)
                .forEach(action -> action.execute(target));
    }

    private <TTarget> boolean testFilter(TTarget target, Collection<ArtResultFilter<TTarget>> filters) {
        return filters.stream().allMatch(filter -> filter.test(target, config));
    }

    @SuppressWarnings("unchecked")
    private <TTarget> boolean testGlobalFilter(TTarget target) {

        if (Objects.isNull(target)) return false;

        return getFilters().entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(target))
                .flatMap(entry -> entry.getValue().stream())
                .map(filter -> (ArtResultFilter<TTarget>) filter)
                .allMatch(filter -> filter.test(target, config));
    }
}
