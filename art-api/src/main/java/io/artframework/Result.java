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

package io.artframework;

import io.artframework.impl.DefaultResult;
import lombok.NonNull;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Result wraps the result status and additional messages.
 * It is immutable, thread safe and does not return any null values.
 */
@Immutable
public interface Result {

    /**
     * Creates a new result from the given parameters.
     *
     * @param status the status of the result
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result of(@NonNull ResultStatus status, @NonNull String... messages) {
        return new DefaultResult(status, messages);
    }

    /**
     * Creates a new result from the boolean parameter.
     * <code>true</code> will result in a success and <code>false</code> in a failure.
     *
     * @param result boolean to create the result from
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result of(boolean result, @NonNull String... messages) {
        return of(result ? ResultStatus.SUCCESS : ResultStatus.FAILURE, messages);
    }

    /**
     * Creates a result that is successful.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result success(String... messages) {
        return of(ResultStatus.SUCCESS, messages);
    }

    /**
     * Creates an empty result that will also return as successful.
     * Use the empty result if there was not valid target or all requirements were filtered out.
     *
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result empty(String... messages) {
        return of(ResultStatus.EMPTY, messages);
    }

    /**
     * Creates a result that is not successful.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result failure(String... messages) {
        return of(ResultStatus.FAILURE, messages);
    }

    /**
     * Creates a result that has an error.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result error(String... messages) {
        return of(ResultStatus.ERROR, messages);
    }

    /**
     * Creates a new cancelled result.
     * A result may be cancelled by events or user actions.
     * <p>
     * You can optionally pass some messages you want to send to the user.
     *
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result cancelled(String... messages) {
        return of(ResultStatus.CANCELLED, messages);
    }

    /**
     * Creates a result that has an error from an exception.
     * The message of the exception will automatically be included in the messages of the result.
     * <p>
     * You can optionally pass some messages you want to send to the user.
     *
     * @param exception the exception that triggered the error
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result error(Exception exception, String... messages) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(messages));
        list.add(exception.getMessage());

        return of(ResultStatus.ERROR, list.toArray(new String[0]));
    }

    /**
     * Returns true if the result is a success.
     * <p>
     * This means that all requirement checks were successful or all actions have been executed.
     * <p>
     * An <code>EMPTY</code> result status will also be a success.
     * A non success (<code>false</code>) does not automatically mean that there was an error.
     * A requirement for example can return a failure if a check failed or an error if a exception or some kind of error occurred.
     * Use the {@link #isError()} method to check if there were actually any errors.
     * <p>
     * This is the same as {@link ResultStatus#isSuccess()}.
     *
     * @return true if the result is a success
     */
    default boolean isSuccess() {
        return getStatus().isSuccess();
    }

    /**
     * Returns true if the result is a failure.
     * <p>
     * This is the direct opposite to {@link #isSuccess()}.
     *
     * @return true if the result is a failure
     */
    default boolean isFailure() {
        return !getStatus().isSuccess();
    }

    /**
     * Returns true if the result is in an error state.
     * <p>
     * This means that one of the called checks thru an exception or encountered some other kind of error.
     * {@link #isSuccess()} will always be false if there were any errors.
     * <p>
     * This is the same as {@link ResultStatus#isError()}.
     *
     * @return true if an error occured
     */
    default boolean isError() {
        return getStatus().isError();
    }

    /**
     * Gets the status of this result.
     * This is never null.
     *
     * @see ResultStatus for more details on the result status codes
     * @return the status of this result that is never null
     */
    ResultStatus getStatus();

    /**
     * Gets all additional messages of this result.
     * <p>
     * These could be error messages or just informative messages that should be send to the user.
     *
     * @return a list of messages that is never null but may be empty
     */
    String[] getMessages();
}
