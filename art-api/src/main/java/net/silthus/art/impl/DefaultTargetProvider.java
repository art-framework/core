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

import lombok.NonNull;
import net.silthus.art.AbstractProvider;
import net.silthus.art.Configuration;
import net.silthus.art.Target;
import net.silthus.art.TargetProvider;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class DefaultTargetProvider extends AbstractProvider implements TargetProvider {

    public DefaultTargetProvider(@NonNull Configuration configuration) {
        super(configuration);
    }

    @Override
    public <TTarget> Optional<Target<TTarget>> get(@Nullable TTarget source) {
        return Optional.empty();
    }

    @Override
    public <TTarget> boolean exists(@Nullable TTarget source) {
        return false;
    }

    @Override
    public <TTarget> TargetProvider add(@NonNull Class<TTarget> sourceClass, @NonNull Function<TTarget, Target<TTarget>> targetProvider) {
        return null;
    }

    @Override
    public <TTarget> TargetProvider remove(@NonNull Class<TTarget> sourceClass) {
        return null;
    }

    @Override
    public TargetProvider removeAll() {
        return null;
    }
}
