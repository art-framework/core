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

import io.artframework.*;
import io.artframework.conf.Constants;
import io.artframework.conf.RequirementConfig;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

/**
 * The requirement context is created for every unique {@link Requirement} configuration.
 * It holds all relevant information to check the requirement and tracks the dependencies.
 *
 * @param <TTarget> target type of the requirement
 */
public class DefaultRequirementContext<TTarget> extends AbstractArtObjectContext<Requirement<TTarget>> implements RequirementContext<TTarget> {

    private final Requirement<TTarget> requirement;
    @Getter
    private final RequirementConfig config;

    public DefaultRequirementContext(
            @NonNull Configuration configuration,
            @NonNull ArtInformation<Requirement<TTarget>> information,
            @NonNull Requirement<TTarget> requirement,
            @NonNull RequirementConfig config
    ) {
        super(configuration, information);
        this.requirement = requirement;
        this.config = config;
    }

    @Override
    public @NonNull String getUniqueId() {
        return getConfig().getIdentifier();
    }

    @Override
    public CombinedResult test(@NonNull Target<TTarget> target, @NonNull ExecutionContext<RequirementContext<TTarget>> context) {

        if (!isTargetType(target.getSource())) return empty();

        if (getConfig().isCheckOnce()) {
            Optional<Boolean> result = store(target, Constants.Storage.CHECK_ONCE_RESULT, Boolean.class);
            if (result.isPresent()) {
                return of(result.get());
            }
        }

        CombinedResult result = of(requirement.test(target, context));

        int currentCount = store(target, Constants.Storage.COUNT, Integer.class).orElse(0);
        if (result.isSuccess()) {
            store(target, Constants.Storage.COUNT, ++currentCount);
        }

        if (getConfig().isCheckOnce()) {
            store(target, Constants.Storage.CHECK_ONCE_RESULT, result);
        }

        if (getConfig().getCount() > 0) {
            result = of(currentCount >= getConfig().getCount()).combine(result);
        }

        if (getConfig().isNegated()) {
            switch (result.getStatus()) {
                case FAILURE:
                    return success();
                case SUCCESS:
                    return failure();
                default:
                case ERROR:
                    return result;
            }
        } else {
            return result;
        }
    }
}
