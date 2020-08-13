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

import io.artframework.finder.AbstractFinderResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Contains all result objects and errors that were found during a find operation with a finder.
 * <p>
 * Each finder result should have a corresponding {@link AbstractFinder} implementation that produces the result.
 * <p>
 * <h4>Implementation notice</h4>
 * The underlying result must be immutable and should only be directly produced by the corresponding finder.
 */
public interface FinderResult<TResult> extends Iterable<TResult> {

    static FinderResult<?> empty() {
        return new AbstractFinderResult<Object>(new ArrayList<>(), new ArrayList<>()) {
            @Override
            public FinderResult<Object> load(Scope scope) {
                return this;
            }
        };
    }

    /**
     * Loads all results into the given configuration instance.
     * <p>
     * Depending on the finder, this might register new modules or load ART objects, etc.
     *
     * @param scope the configuration instance to load the results into
     * @return this finder result
     */
    FinderResult<TResult> load(Scope scope);

    /**
     * Returns a list of all classes excluding any classes that had errors.
     * Use the {@link #errors()} method to get all classes that had errors
     * while searching for {@link ArtObject}s.
     *
     * @return a list of all results found by the finder
     */
    Collection<TResult> results();

    /**
     * Iterates every result in this result set and applies the given consumer to it.
     *
     * @param consumer the result handler
     * @return this result
     */
    FinderResult<TResult> forEachResult(Consumer<TResult> consumer);

    /**
     * @return a list of all errors that occurred during the find operation
     */
    Collection<ArtObjectError> errors();

    /**
     * Gives the option to handle the errors in a fluent syntax style.
     * You can use it to print log messages or handle and add the error classes
     * in an other way.
     *
     * @param consumer the error handler
     * @return this result
     */
    FinderResult<TResult> forEachError(Consumer<ArtObjectError> consumer);
}
