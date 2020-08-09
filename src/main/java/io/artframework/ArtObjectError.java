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
import lombok.Value;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Optional;

/**
 * Container that wraps errors that can occur when finding and
 * register {@link ArtObject}s with the {@link AbstractFinder}.
 */
@Value
@Accessors(fluent = true)
public class ArtObjectError {

    public static ArtObjectError of(String message, Reason reason, Class<? extends ArtObject> artObject) {
        return of(message, reason, artObject, null);
    }

    public static ArtObjectError of(String message, Reason reason, Class<? extends ArtObject> artObject, @Nullable Exception exception) {
        return new ArtObjectError(message, reason, artObject, exception);
    }

    public static ArtObjectError of(Reason reason, Class<? extends ArtObject> artObject, @NonNull Exception exception) {
        return new ArtObjectError(exception.getMessage(), reason, artObject, exception);
    }

    /**
     * A descriptive message why this {@link ArtObject} cannot be
     * registered.
     */
    String message;
    /**
     * The error code or reason for the error.
     */
    Reason reason;
    /**
     * The actual class of the {@link ArtObject} that produced the error.
     */
    Class<? extends ArtObject> artObject;
    /**
     * Returns the source file of the underlying {@link ArtObject}.
     * This is either a class file in a path or a JAR file containing the class.
     */
    URL location;
    /**
     * An optional exception that occured.
     */
    Exception exception;

    private ArtObjectError(String message, Reason reason, Class<? extends ArtObject> artObject, @Nullable Exception exception) {
        this.message = message;
        this.reason = reason;
        this.artObject = artObject;
        this.location = artObject.getProtectionDomain().getCodeSource().getLocation();
        this.exception = exception;
    }

    public Optional<Exception> exception() {
        return Optional.ofNullable(exception);
    }

    public enum Reason {
        UNKNOWN,
        NO_IDENTIFIER,
        INVALID_CONSTRUCTOR,
        NO_ANNOTATION,
        INVALID_CONFIG,
        INVALID_ART_OBJECT
    }
}
