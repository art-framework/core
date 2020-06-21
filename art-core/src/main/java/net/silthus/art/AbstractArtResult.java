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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;

import java.util.List;

@Getter(AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class AbstractArtResult implements ArtResult {

    private final ArtConfig config;
    private final List<ArtContext<?, ?>> art;

    public AbstractArtResult(ArtConfig config, List<ArtContext<?, ?>> art) {
        this.config = config;
        this.art = ImmutableList.copyOf(art);
    }

    protected abstract <TTarget> boolean filter(TTarget target, ArtContext<TTarget, ?> context);

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> boolean test(TTarget target) {

        return getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof RequirementContext)
                .map(artContext -> (RequirementContext<TTarget, ?>) artContext)
                .filter(requirement -> filter(target, requirement))
                .allMatch(requirement -> requirement.test(target));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <TTarget> void execute(TTarget target) {

        getArt().stream()
                .filter(artContext -> artContext.isTargetType(target))
                .filter(artContext -> artContext instanceof ActionContext)
                .map(artContext -> (ActionContext<TTarget, ?>) artContext)
                .filter(action -> filter(target, action))
                .forEach(action -> action.execute(target));
    }
}
