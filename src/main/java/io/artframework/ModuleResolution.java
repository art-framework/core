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

import javax.annotation.Nullable;
import java.util.Optional;

public class ModuleResolution {

    private final Result result;
    private final String message;
    private final Exception exception;

    public ModuleResolution(@NonNull Result result) {
        this(result, null, null);
    }

    public ModuleResolution(@NonNull Result result, @NonNull String message) {
        this(result, message, null);
    }

    public ModuleResolution(@NonNull Result result, @NonNull Exception exception) {
        this(result, exception.getMessage(), exception);
    }

    public ModuleResolution(@NonNull Result result, @Nullable String message, @Nullable Exception exception) {
        this.result = result;
        this.message = message;
        this.exception = exception;
    }

    public Result result() {
        return result;
    }

    @Nullable
    public String message() {
        return message;
    }

    public Optional<Exception> exception() {
        return Optional.ofNullable(exception);
    }

    public enum Result {
        SUCCESS,
        DELAYED,
        NOT_FOUND,
        FAILED
    }
}
