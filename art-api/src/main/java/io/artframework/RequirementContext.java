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

package io.artframework;

import io.artframework.conf.RequirementConfig;
import io.artframework.impl.DefaultRequirementContext;
import lombok.NonNull;

/**
 * The {@link RequirementContext} holds all of the information to execute the contained {@link Requirement}.
 * It also provides a lot of useful additional features like storing data for the context of the
 * requirement execution or accessing the {@link Configuration}.
 *
 * @param <TTarget> type of the target that is targeted by the {@link Requirement}
 */
public interface RequirementContext<TTarget> extends Requirement<TTarget>, ArtObjectContext<Requirement<TTarget>> {

    static <TTarget> RequirementContext<TTarget> of(
            @NonNull Configuration configuration,
            @NonNull Options<Requirement<TTarget>> information,
            @NonNull Requirement<TTarget> requirement,
            @NonNull RequirementConfig config
    ) {
        return new DefaultRequirementContext<>(configuration, information, requirement, config);
    }

    /**
     * Gets the {@link RequirementConfig} used by the {@link Requirement}.
     *
     * @return the underlying {@link RequirementConfig}
     */
    RequirementConfig config();

    @Override
    CombinedResult test(@NonNull Target<TTarget> target, @NonNull ExecutionContext<RequirementContext<TTarget>> context);
}
