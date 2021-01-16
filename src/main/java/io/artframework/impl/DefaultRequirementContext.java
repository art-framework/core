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

import com.google.common.base.Strings;
import io.artframework.*;
import io.artframework.conf.Constants;
import io.artframework.conf.RequirementConfig;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Optional;

/**
 * The requirement context is created for every unique {@link Requirement} configuration.
 * It holds all relevant information to check the requirement and tracks the dependencies.
 *
 * @param <TTarget> target type of the requirement
 */
@Accessors(fluent = true)
public class DefaultRequirementContext<TTarget> extends AbstractArtObjectContext<Requirement<TTarget>> implements RequirementContext<TTarget>, CombinedResultCreator {

    private final Requirement<TTarget> requirement;
    @Getter
    private final RequirementConfig config;

    public DefaultRequirementContext(
            @NonNull Scope scope,
            @NonNull ArtObjectMeta<Requirement<TTarget>> information,
            @NonNull Requirement<TTarget> requirement,
            @NonNull RequirementConfig config
    ) {
        super(scope, information);
        this.requirement = requirement;
        this.config = config;
    }

    @Override
    public String uniqueId() {

        if (Strings.isNullOrEmpty(config().identifier())) {
            return super.uniqueId();
        }

        return config().identifier();
    }

    @Override
    public TargetResult<TTarget, RequirementContext<TTarget>> test(@NonNull Target<TTarget> target, @NonNull ExecutionContext<RequirementContext<TTarget>> context) {

        if (!isTargetType(target.source())) return empty().with(target, this);

        if (config().checkOnce()) {
            Optional<Boolean> result = store(target, Constants.Storage.CHECK_ONCE_RESULT, Boolean.class);
            if (result.isPresent()) {
                return resultOf(result.get()).with(target, this);
            }
        }

        Result result = resultOf(requirement.test(target, context));

        int currentCount = store(target, Constants.Storage.COUNT, Integer.class).orElse(0);
        if (result.success()) {
            store(target, Constants.Storage.COUNT, ++currentCount);
        }

        if (config().checkOnce()) {
            store(target, Constants.Storage.CHECK_ONCE_RESULT, result);
        }

        if (config().count() > 0) {
            result = resultOf(currentCount >= config().count()).combine(result);
        }

        if (config().negated()) {
            switch (result.status()) {
                case FAILURE:
                    return success().with(target, this);
                case SUCCESS:
                    return failure().with(target, this);
                default:
                case ERROR:
                    return result.with(target, this);
            }
        } else {
            return result.with(target, this);
        }
    }
}
