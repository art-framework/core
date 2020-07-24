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

public enum ResultStatus {
    /**
     * There were no requirements to check.
     * <p>
     * This can be the case if all requirements target a different target type or are filtered out.
     * The <code>EMPTY</code> result is equivalent to a <code>SUCCESS</code> and will return true in {@link Result#isSuccess()}.
     */
    EMPTY,
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

    public ResultStatus combine(ResultStatus result) {

        if (this == ERROR || result == ERROR) {
            return ERROR;
        } else if (this == FAILURE || result == FAILURE) {
            return FAILURE;
        } else if (this == EMPTY && result == EMPTY) {
            return EMPTY;
        }

        return SUCCESS;
    }

    public boolean isSuccess() {
        return this == SUCCESS || this == EMPTY;
    }

    public boolean isError() {
        return this == ERROR;
    }
}
