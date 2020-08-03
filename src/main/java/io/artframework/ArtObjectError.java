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

import lombok.Getter;

import java.net.URL;

/**
 * Container that wraps errors that can occur when finding and
 * register {@link ArtObject}s with the {@link Finder}.
 */
@Getter
public class ArtObjectError {

    public static ArtObjectError of(String message, Reason reason, Class<? extends ArtObject> artObject) {
        return new ArtObjectError(message, reason, artObject);
    }

    /**
     * A descriptive message why this {@link ArtObject} cannot be
     * registered.
     */
    private final String message;
    /**
     * The error code or reason for the error.
     */
    private final Reason reason;
    /**
     * The actual class of the {@link ArtObject} that produced the error.
     */
    private final Class<? extends ArtObject> artObject;
    /**
     * Returns the source file of the underlying {@link ArtObject}.
     * This is either a class file in a path or a JAR file containing the class.
     */
    private final URL location;

    private ArtObjectError(String message, Reason reason, Class<? extends ArtObject> artObject) {
        this.message = message;
        this.reason = reason;
        this.artObject = artObject;
        this.location = artObject.getProtectionDomain().getCodeSource().getLocation();
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
