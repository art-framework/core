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
public interface Result extends Scope {

    /**
     * Creates a new result from the given parameters using the global configuration.
     *
     * @param status the status of the result
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result of(@NonNull ResultStatus status, @NonNull String... messages) {
        return of(ART.configuration(), status, messages);
    }

    /**
     * Creates a new result from the given parameters.
     *
     * @param configuration the configuration of the scope
     * @param status the status of the result
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result of(@NonNull Configuration configuration, @NonNull ResultStatus status, @NonNull String... messages) {
        return new DefaultResult(configuration, status, messages);
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
     * Creates a new result from the boolean parameter with the given configuration.
     * <code>true</code> will result in a success and <code>false</code> in a failure.
     *
     * @param configuration the configuration of the scope
     * @param result boolean to create the result from
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result of(@NonNull Configuration configuration, boolean result, @NonNull String... messages) {
        return of(configuration, result ? ResultStatus.SUCCESS : ResultStatus.FAILURE, messages);
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
     * Creates a result that is successful with the given configuration.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param configuration the configuration of the scope
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result success(@NonNull Configuration configuration, String... messages) {
        return of(configuration, ResultStatus.SUCCESS, messages);
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
     * Creates an empty result that will also return as successful.
     * Use the empty result if there was not valid target or all requirements were filtered out.
     *
     * @param configuration the configuration of the scope
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result empty(@NonNull Configuration configuration, String... messages) {
        return of(configuration, ResultStatus.EMPTY, messages);
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
     * Creates a result that is not successful.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param configuration the configuration of the scope
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result failure(@NonNull Configuration configuration, String... messages) {
        return of(configuration, ResultStatus.FAILURE, messages);
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
     * Creates a result that has an error.
     * You can optionally pass some messages you want to send to the user.
     *
     * @param configuration the configuration of the scope
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result error(@NonNull Configuration configuration, String... messages) {
        return of(configuration, ResultStatus.ERROR, messages);
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
     * Creates a new cancelled result.
     * A result may be cancelled by events or user actions.
     * <p>
     * You can optionally pass some messages you want to send to the user.
     *
     * @param configuration the configuration of the scope
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result cancelled(@NonNull Configuration configuration, String... messages) {
        return of(configuration, ResultStatus.CANCELLED, messages);
    }

    /**
     * Creates a result that has an error from an exception.
     * The message of the exception will automatically be included in the messages of the result.
     * <p>
     * You can optionally pass some messages you want to send to the user.
     *
     * @param configuration the configuration of the scope
     * @param exception the exception that triggered the error
     * @param messages additional messages of the result
     * @return the created result
     */
    static Result error(@NonNull Configuration configuration, Exception exception, String... messages) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(messages));
        list.add(exception.getMessage());

        return of(configuration, ResultStatus.ERROR, list.toArray(new String[0]));
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
     * Use the {@link #error()} method to check if there were actually any errors.
     * <p>
     * This is the same as {@link ResultStatus#isSuccess()}.
     *
     * @return true if the result is a success
     */
    default boolean success() {
        return status().isSuccess();
    }

    /**
     * Returns true if the result is a failure.
     * <p>
     * This is the direct opposite to {@link #success()}.
     *
     * @return true if the result is a failure
     */
    default boolean failure() {
        return !status().isSuccess();
    }

    /**
     * Returns true if the result is in an error state.
     * <p>
     * This means that one of the called checks thru an exception or encountered some other kind of error.
     * {@link #success()} will always be false if there were any errors.
     * <p>
     * This is the same as {@link ResultStatus#isError()}.
     *
     * @return true if an error occured
     */
    default boolean error() {
        return status().isError();
    }

    /**
     * Gets the status of this result.
     * This is never null.
     *
     * @see ResultStatus for more details on the result status codes
     * @return the status of this result that is never null
     */
    ResultStatus status();

    /**
     * Gets all additional messages of this result.
     * <p>
     * These could be error messages or just informative messages that should be send to the user.
     *
     * @return a list of messages that is never null but may be empty
     */
    String[] messages();

    /**
     * Creates a new targeted result from this result with the given target and context.
     *
     * @param target the target of the result
     * @param context the context of the result
     * @param <TTarget> the target type
     * @return a new target result based of this result with the given target and context
     */
    default <TTarget, TContext extends ArtObjectContext<?>> TargetResult<TTarget, TContext>
    with(@NonNull Target<TTarget> target, @NonNull TContext context) {
        return TargetResult.of(this, target, context);
    }

    /**
     * Combines this result with the given result returning a new combined result.
     * <p>
     * This will use the {@link ResultStatus#combine(ResultStatus)} method to combine the two result statuses
     * and then merge the messages. Duplicate messages will be omitted.
     *
     * @param result the result to combine with this result
     * @return the new combined result
     */
    default CombinedResult combine(Result result) {
        return CombinedResult.of(this, result);
    }

    default FutureResult future() {
        if (this instanceof FutureResult) {
            return (FutureResult) this;
        }
        return FutureResult.of(this);
    }

    default CombinedResult combine() {
        if (this instanceof CombinedResult) {
            return (CombinedResult) this;
        }
        return CombinedResult.of(this);
    }
}
