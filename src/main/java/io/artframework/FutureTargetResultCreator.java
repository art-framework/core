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

public interface FutureTargetResultCreator {

    default FutureResult of(@NonNull ResultStatus status, Target<?> target, ArtObjectContext<?> context, @NonNull String... messages) {
        return FutureResult.of(CombinedResult.of(Result.of(status, messages).with(target, context)));
    }

    default FutureResult of(boolean result, Target<?> target, ArtObjectContext<?> context,  @NonNull String... messages) {
        return FutureResult.of(CombinedResult.of(Result.of(result, messages).with(target, context)));
    }

    default FutureResult of(Result result, Target<?> target, ArtObjectContext<?> context) {
        return FutureResult.of(result.with(target, context));
    }

    default FutureResult success(Target<?> target, ArtObjectContext<?> context, String... messages) {
        return FutureResult.of(CombinedResult.of(Result.success(messages).with(target, context)));
    }

    default FutureResult empty(Target<?> target, ArtObjectContext<?> context, String... messages) {
        return FutureResult.of(CombinedResult.empty(messages).with(target, context));
    }

    default FutureResult failure(Target<?> target, ArtObjectContext<?> context, String... messages) {
        return FutureResult.of(CombinedResult.of(Result.failure(messages).with(target, context)));
    }

    default FutureResult error(Target<?> target, ArtObjectContext<?> context, String... messages) {
        return FutureResult.of(CombinedResult.of(Result.error(messages).with(target, context)));
    }

    default FutureResult error(Exception exception, Target<?> target, ArtObjectContext<?> context, String... messages) {
        return FutureResult.of(CombinedResult.of(Result.error(exception, messages).with(target, context)));
    }

    default FutureResult cancelled(Target<?> target, ArtObjectContext<?> context, String... messages) {
        return FutureResult.of(CombinedResult.of(Result.cancelled(messages).with(target, context)));
    }
}
