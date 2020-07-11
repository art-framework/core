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

package net.silthus.art.api.requirements;

import lombok.Getter;
import lombok.NonNull;
import net.silthus.art.Requirement;
import net.silthus.art.Storage;
import net.silthus.art.Target;
import net.silthus.art.api.ArtContext;
import net.silthus.art.api.storage.StorageConstants;

import java.util.Objects;
import java.util.Optional;

/**
 * The requirement context is created for every unique {@link Requirement} configuration.
 * It holds all relevant information to check the requirement and tracks the dependencies.
 *
 * @param <TTarget> target type of the requirement
 * @param <TConfig> config type of the requirement
 */
public class RequirementContext<TTarget, TConfig> extends ArtContext<TTarget, TConfig, RequirementConfig<TConfig>> implements Requirement<TTarget, TConfig> {

    @Getter
    private final Requirement<TTarget, TConfig> requirement;

    public RequirementContext(Class<TTarget> targetClass, Requirement<TTarget, TConfig> requirement, RequirementConfig<TConfig> config, Storage storage) {
        super(storage, targetClass, config);
        Objects.requireNonNull(requirement, "requirement must not be null");
        this.requirement = requirement;
    }

    public final boolean test(@NonNull Target<TTarget> target) {

        return test(target, this);
    }

    @Override
    public boolean test(@NonNull Target<TTarget> target, RequirementContext<TTarget, TConfig> context) {

        if (context != null && context != this)
            throw new UnsupportedOperationException("RequirementContext#test(target, context) must not be called directly. Use ActionResult#test(target) instead.");

        if (!isTargetType(target.getSource())) return true;

        if (getOptions().isCheckOnce()) {
            Optional<Boolean> result = getStorage().get(this, target, StorageConstants.CHECK_ONCE_RESULT, Boolean.class);
            if (result.isPresent()) {
                return result.get();
            }
        }

        boolean result = getRequirement().test(target, Objects.isNull(context) ? this : context);

        int currentCount = getStorage().get(this, target, StorageConstants.COUNT, Integer.class).orElse(0);
        if (result) {
            getStorage().data(this, target, StorageConstants.COUNT, ++currentCount);
        }

        if (getOptions().isCheckOnce()) {
            getStorage().data(this, target, StorageConstants.CHECK_ONCE_RESULT, result);
        }

        if (getOptions().getCount() > 0) {
            result = currentCount >= getOptions().getCount();
        }

        if (getOptions().isNegated()) {
            return !result;
        } else {
            return result;
        }
    }
}
