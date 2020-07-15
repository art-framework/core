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

import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.*;
import net.silthus.art.api.AbstractArtObjectContext;
import net.silthus.art.api.storage.StorageConstants;
import net.silthus.art.conf.RequirementConfig;

import java.util.Optional;

/**
 * The requirement context is created for every unique {@link Requirement} configuration.
 * It holds all relevant information to check the requirement and tracks the dependencies.
 *
 * @param <TTarget> target type of the requirement
 */
public class DefaultRequirementContext<TTarget> extends AbstractArtObjectContext implements RequirementContext<TTarget> {

    private final Requirement<TTarget> requirement;
    @Getter
    private final RequirementConfig config;

    public DefaultRequirementContext(
            @NonNull Configuration configuration,
            @NonNull Class<?> targetClass,
            @NonNull Requirement<TTarget> requirement,
            @NonNull RequirementConfig config
    ) {
        super(configuration, targetClass);
        this.requirement = requirement;
        this.config = config;
    }

    @Override
    public @NonNull String getUniqueId() {
        return getConfig().getIdentifier();
    }

    @Override
    public boolean test(ExecutionContext<TTarget, RequirementContext<TTarget>> context) {

        Target<TTarget> target = context.target();

        if (!isTargetType(target.getSource())) return true;

        if (getConfig().isCheckOnce()) {
            Optional<Boolean> result = store(target, StorageConstants.CHECK_ONCE_RESULT, Boolean.class);
            if (result.isPresent()) {
                return result.get();
            }
        }

        boolean result = context.test(this, requirement);

        int currentCount = store(target, StorageConstants.COUNT, Integer.class).orElse(0);
        if (result) {
            store(target, StorageConstants.COUNT, ++currentCount);
        }

        if (getConfig().isCheckOnce()) {
            store(target, StorageConstants.CHECK_ONCE_RESULT, result);
        }

        if (getConfig().getCount() > 0) {
            result = currentCount >= getConfig().getCount();
        }

        if (getConfig().isNegated()) {
            return !result;
        } else {
            return result;
        }
    }
}
