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

import net.silthus.art.events.DefaultTargetedTestResult;

import java.util.Optional;

public interface TargetedTestResult<TTarget> extends TestResult {

    static <TTarget> TargetedTestResult<TTarget> of(Target<TTarget> target, ResultStatus result) {
        return new DefaultTargetedTestResult<>(result, target);
    }

    static <TTarget> TargetedTestResult<TTarget> of(Class<TTarget> targetClass, ResultStatus result) {
        return new DefaultTargetedTestResult<>(result, targetClass);
    }

    Optional<Target<TTarget>> getTarget();

    Class<TTarget> getTargetClass();
}
