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

public interface TestResult extends ArtResult {

    static <TTarget> TargetedTestResult<TTarget> of(Target<TTarget> target, Result result) {
        return TargetedTestResult.of(target, result);
    }

    static TestResult of(Result result) {
        return new DefaultTestResult(result);
    }

    static TestResult success() {
        return of(Result.SUCCESS);
    }

    static TestResult failure(String... reasons) {
        return new DefaultTestResult(Result.FAILURE, reasons, null);
    }

    static TestResult error(ErrorCode errorCode, String... reasons) {
        return new DefaultTestResult(Result.ERROR, null, reasons);
    }

    Result getResult();

    Map<Target<?>, Result> getResults();

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

    <TTarget> TestResult combine(@NonNull Target<TTarget> target);

    enum Result {
        /**
         * The requirement(s) were all successfully checked.
         * This will only be the case if all underlying requirements AND target checks are successful.
         */
        SUCCESS,
        /**
         * One of the requirements was not successfully checked.
         * <p>
         * As soon as anyone of the underlying requirement checks fails, the whole TestResult fails.
         * You can get the failure reasons with {@link #getFailureReasons()}.
         * <p>
         * You can still get the individual results for each target by calling {@link #forTarget(Target)#getResult()}
         * and the result for all underlying requirements by calling {@link #getResults()}.
         */
        FAILURE,
        /**
         * Any of the requirement checks had an error.
         * <p>
         * When any of the checks for any requirement and any target errors the whole TestResult will be in an error state.
         * You can get the error reasons with {@link #getErrorReasons()}.
         * <p>
         * You can still check the outcome for each target by calling {@link #forTarget(Target)}.
         */
        ERROR;

        public Result combine(Result result) {

            if (this == ERROR || result == ERROR) {
                return ERROR;
            } else if (this == FAILURE || result == FAILURE) {
                return FAILURE;
            }

            return SUCCESS;
        }
    }
}
