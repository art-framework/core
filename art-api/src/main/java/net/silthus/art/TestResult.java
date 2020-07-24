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

import lombok.NonNull;
import net.silthus.art.events.DefaultTestResult;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface TestResult {

    static <TTarget> TargetedTestResult<TTarget> of(Target<TTarget> target, ResultStatus result) {
        return TargetedTestResult.of(target, result);
    }

    static TestResult of(ResultStatus result) {
        return new DefaultTestResult(result);
    }

    static TestResult success() {
        return of(ResultStatus.SUCCESS);
    }

    static TestResult empty() {
        return of(ResultStatus.EMPTY);
    }

    static TestResult failure(String... reasons) {
        return new DefaultTestResult(ResultStatus.FAILURE, reasons, null);
    }

    static TestResult error(ErrorCode errorCode, String... reasons) {
        return new DefaultTestResult(ResultStatus.ERROR, null, reasons);
    }

    ResultStatus getResult();

    Map<Target<?>, ResultStatus> getResults();

    boolean isSuccessful();

    boolean hasError();

    Collection<String> getFailureReasons();

    Collection<String> getErrorReasons();

    <TTarget> Optional<TargetedTestResult<TTarget>> forTarget(Target<TTarget> target);

    <TTarget> Optional<TargetedTestResult<TTarget>> forTarget(Class<TTarget> targetClass);

    /**
     * Combines this result with the result given as the parameter.
     *
     * @param result the result to combine with this result
     * @return a new result that holds both results
     */
    TestResult combine(@NonNull TestResult result);

    <TTarget> TestResult addTarget(@NonNull Target<TTarget> target);

}
