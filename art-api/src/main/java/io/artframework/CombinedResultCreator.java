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

import lombok.NonNull;

public interface CombinedResultCreator extends ResultCreator {

    default CombinedResult of(@NonNull Result result) {
        return CombinedResult.of(result);
    }

    @Override
    default CombinedResult of(@NonNull ResultStatus status, @NonNull String... messages) {
        return CombinedResult.of(Result.of(status, messages));
    }

    @Override
    default CombinedResult of(boolean result, @NonNull String... messages) {
        return CombinedResult.of(Result.of(result, messages));
    }

    @Override
    default CombinedResult success(String... messages) {
        return CombinedResult.of(Result.success(messages));
    }

    @Override
    default CombinedResult empty(String... messages) {
        return CombinedResult.empty(messages);
    }

    @Override
    default CombinedResult failure(String... messages) {
        return CombinedResult.of(Result.failure(messages));
    }

    @Override
    default CombinedResult error(String... messages) {
        return CombinedResult.of(Result.error(messages));
    }

    @Override
    default CombinedResult error(Exception exception, String... messages) {
        return CombinedResult.of(Result.error(exception, messages));
    }

    @Override
    default CombinedResult cancelled(String... messages) {
        return CombinedResult.of(Result.cancelled(messages));
    }
}
