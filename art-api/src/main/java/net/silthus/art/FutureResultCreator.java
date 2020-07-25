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

public interface FutureResultCreator extends CombinedResultCreator {

    @Override
    default FutureResult of(@NonNull ResultStatus status, @NonNull String... messages) {
        return FutureResult.of(CombinedResult.of(Result.of(status, messages)));
    }

    @Override
    default FutureResult of(boolean result, @NonNull String... messages) {
        return FutureResult.of(CombinedResult.of(Result.of(result, messages)));
    }

    @Override
    default FutureResult success(String... messages) {
        return FutureResult.of(CombinedResult.of(Result.success(messages)));
    }

    @Override
    default FutureResult empty(String... messages) {
        return FutureResult.of(CombinedResult.empty(messages));
    }

    @Override
    default FutureResult failure(String... messages) {
        return FutureResult.of(CombinedResult.of(Result.failure(messages)));
    }

    @Override
    default FutureResult error(String... messages) {
        return FutureResult.of(CombinedResult.of(Result.error(messages)));
    }

    @Override
    default FutureResult error(Exception exception, String... messages) {
        return FutureResult.of(CombinedResult.of(Result.error(exception, messages)));
    }

    @Override
    default FutureResult cancelled(String... messages) {
        return FutureResult.of(CombinedResult.of(Result.cancelled(messages)));
    }
}
