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

import java.util.Collection;
import java.util.function.Consumer;

/**
 * The future result is used in situations where the underlying actions or trigger
 * do not immediately return all of their results.
 * This may be the case if an action or trigger is delayed or executed in parallel.
 * <p>
 * Use the {@link #onCompletion(Consumer)} method to subscribe to the future and get
 * notified once the result is completed.
 * You can also use this method even if the result is already completed and will get and immediate callback.
 * <p>
 * You also have all of the available options from a normal {@link CombinedResult} and can check the
 * intermediate state of this result with the normal methods.
 * This will always only reflect the point in time were this future result was created. It will never
 * reflect the state in between the creation and completion of the future result.
 */
public interface FutureResult extends CombinedResult {

    /**
     * Creates a new uncompleted future result.
     *
     * @return a new empty future result
     */
    static FutureResult empty() {
        return new DefaultFutureResult();
    }

    /**
     * Creates a new future result with the given result as starting point.
     *
     * @param result the result to create this future result with
     * @return a new future result
     */
    static FutureResult of(CombinedResult result) {
        return new DefaultFutureResult(result);
    }

    /**
     * Checks if this future result is complete.
     * This means that {@link #onCompletion(Consumer)} has been called and the execution is finished.
     *
     * @return false if the result is incomplete and waits for the rest of the results
     */
    boolean isComplete();

    /**
     * Use this callback to react to the completion of this future result.
     * It will be called once the result is complete or immediately if the result is already complete.
     *
     * @param result the callback that consumes the final result
     */
    void onCompletion(Consumer<CombinedResult> result);

    /**
     * Completes this future result and combines the current result with the results from the future.
     *
     * @param futureResult the future result that should be combined with this result and returned to the callback.
     * @return the combined result from the future and present
     */
    CombinedResult complete(CombinedResult futureResult);

    /**
     * Gets a list of all subscribed callbacks of this future result.
     * The list is immutable and must not be changed.
     *
     * @return an immutable list of all callbacks
     */
    Collection<Consumer<CombinedResult>> getConsumers();

    /**
     * Combines this future result with the given result.
     * The consumers of both results will be combined if the given result is also a future result.
     * Will return a new future result and leave this result unmodified.
     *
     * @param result the result to combine with this result
     * @return a new future result with combined consumers
     */
    @Override
    FutureResult combine(Result result);
}
