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

import io.artframework.annotations.ART;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Contains all of the classes that were found when searching the classpath
 * or a file for {@link ArtObject}s.
 * Classes that don't have an @{@link ART} annotation
 * or public parameterless constructor can be found in the
 */
public interface ArtFinderResult extends Iterable<ArtInformation> {

    /**
     * @return the {@link ArtFinder} that created this result
     */
    ArtFinder finder();

    /**
     * Adds all classes that do not have an error to the {@link ArtProvider}
     * by calling {@link ArtProvider#addAll(Collection)}.
     *
     * @return the {@link ArtFinder} that created this result
     */
    default ArtFinder register() {
        finder().addAll(getAll());
        return finder();
    }

    ArtFinderResult filter(Predicate<ArtInformation<?>> predicate);

    Stream<ArtInformation<?>> stream();

    /**
     * Returns a list of all classes excluding any classes that had errors.
     * Use the {@link #getErrors()} method to get all classes that had errors
     * while searching for {@link ArtObject}s.
     *
     * @return a list of all classes found by the {@link ArtFinder}
     */
    Collection<ArtInformation<?>> getAll();

    /**
     * Gives the option to handle the errors in a fluent syntax style.
     * You can use it to print log messages or handle and add the error classes
     * in an other way.
     *
     * @param consumer the error handler
     * @return this {@link ArtFinderResult}
     */
    ArtFinderResult errors(Consumer<ArtObjectError> consumer);

    Stream<ArtObjectError> errorStream();

    Collection<ArtObjectError> getErrors();
}
