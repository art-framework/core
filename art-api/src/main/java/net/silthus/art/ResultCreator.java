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

public interface ResultCreator {

    /**
     * Creates a new result from the given parameters.
     *
     * @param status the status of the result
     * @param messages additional messages of the result
     * @return the created result
     * @see Result#of(ResultStatus, String...)
     */
    default Result of(@NonNull ResultStatus status, @NonNull String... messages) {
        return Result.of(status, messages);
    }

    /**
     * Creates a new result from the boolean parameter.
     * <code>true</code> will result in a success and <code>false</code> in a failure.
     *
     * @param result boolean to create the result from
     * @param messages additional messages of the result
     * @return the created result
     * @see Result#of(boolean, String...)
     */
    default Result of(boolean result, @NonNull String... messages) {
        return Result.of(result ? ResultStatus.SUCCESS : ResultStatus.FAILURE, messages);
    }

    /**
     * Creates a result that is successful.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param messages to send to the user
     * @return the created result
     * @see Result#success(String...)
     */
    default Result success(String... messages) {
        return Result.success(messages);
    }

    /**
     * Creates an empty result that will also return as successful.
     * Use the empty result if there was not valid target or all requirements were filtered out.
     *
     * @param messages additional messages of the result
     * @return the created result
     * @see Result#empty(String...)
     */
    default Result empty(String... messages) {
        return Result.empty(messages);
    }

    /**
     * Creates a result that is not successful.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param messages to send to the user
     * @return the created result
     * @see Result#failure(String...)
     */
    default Result failure(String... messages) {
        return Result.failure(messages);
    }

    /**
     * Creates a result that has an error.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param messages to send to the user
     * @return the created result
     * @see Result#error(String...)
     */
    default Result error(String... messages) {
        return Result.error(messages);
    }

    /**
     * Creates a result that has an error from an exception.
     * The message of the exception will automatically be included in the messages of the result.
     * <p>
     * You can optionally pass some messages you want to send to the user.
     *
     * @param exception the exception that triggered the error
     * @param messages to send to the user
     * @return the created result
     * @see Result#error(Exception, String...)
     */
    default Result error(Exception exception, String... messages) {
        return Result.error(exception, messages);
    }

    /**
     * Creates a new cancelled result.
     * A result may be cancelled by events or user actions.
     * <p>
     * You can optionally pass some messages you want to send to the user.
     *
     * @param messages additional messages of the result
     * @return the created result
     * @see Result#cancelled(String...)
     */
    default Result cancelled(String... messages) {
        return Result.cancelled(messages);
    }
}
