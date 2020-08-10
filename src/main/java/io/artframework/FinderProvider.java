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

import io.artframework.impl.DefaultFinderProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Provides the option to register various finder implementations that can load different parts of the art-framework.
 * <p>
 * Implement the {@link AbstractFinder} and {@link FinderResult} interface to create your own finders
 * and then register them with this provider.
 */
public interface FinderProvider extends Provider {

    /**
     * Creates a new default finder provider with the given configuration.
     *
     * @param configuration the configuration instance to use
     * @return the default finder provider
     */
    static FinderProvider of(Configuration configuration) {
        return new DefaultFinderProvider(configuration);
    }

    /**
     * Adds the given finder to this finder provider if it does not exist.
     * <p>
     * This means that the finder will be called on all @{code findAllIn(...)} methods in this provider.
     * <p>
     * Nothing will happen if the finder instance has already been added to this provider.
     *
     * @param finder the finder to register
     * @return this finder provider
     */
    FinderProvider add(Finder finder);

    /**
     * Adds the given finder from this provider if it exists.
     * <p>
     * Nothing will happen if the finder instance has not been added to this provider.
     *
     * @param finder the finder to remove
     * @return this finder provider
     */
    FinderProvider remove(Finder finder);

    /**
     * Removes all finders from this provider.
     *
     * @return this provider
     */
    FinderProvider clear();

    /**
     * @return all finders that are currently registered in this provider
     */
    Collection<Finder> all();

    /**
     * Aggregates the find results of all finders registered in this provider.
     * <p>
     * This will call the {@link Finder#findAllIn(Path)} method on all registered finders and return all results.
     *
     * @param path the path or file to search in
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllIn(Path path) {
        return all().stream()
                .map(finder -> finder.findAllIn(path))
                .collect(Collectors.toList());
    }

    /**
     * Aggregates the find results of all finders registered in this provider and calls the load method on all results.
     * <p>
     * This will call the {@link Finder#findAllIn(Path)} method on all registered finders
     * and then call {@link FinderResult#load(Configuration)} on all of them returning the result.
     *
     * @param path the path or file to search in
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllAndLoadIn(Path path) {
        Collection<FinderResult<?>> results = findAllIn(path);
        results.forEach(result -> result.load(configuration()));
        return results;
    }

    /**
     * Aggregates the find results of all finders registered in this provider
     * filtering the files based on the given predicate.
     * <p>
     * This will call the {@link Finder#findAllIn(Path)} method on all registered finders and return all results.
     *
     * @param path the path or file to search in
     * @param predicate the predicate used to filter the files
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllIn(Path path, Predicate<File> predicate) {
        return all().stream()
                .map(finder -> finder.findAllIn(path, predicate))
                .collect(Collectors.toList());
    }

    /**
     * Aggregates the find results of all finders registered in this provider and calls the load method on all results
     * filtering the files based on the given predicate.
     * <p>
     * This will call the {@link Finder#findAllIn(Path)} method on all registered finders
     * and then call {@link FinderResult#load(Configuration)} on all of them returning the result.
     *
     * @param path the path or file to search in
     * @param predicate the predicate used to filter the files
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllAndLoadIn(Path path, Predicate<File> predicate) {
        Collection<FinderResult<?>> results = findAllIn(path, predicate);
        results.forEach(result -> result.load(configuration()));
        return results;
    }
}
