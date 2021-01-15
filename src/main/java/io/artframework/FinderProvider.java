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
     * @param scope the scope of the provider
     * @return the default finder provider
     */
    static FinderProvider of(Scope scope) {
        return new DefaultFinderProvider(scope);
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
     * This will call the {@link Finder#findAllIn(ClassLoader, File)} method on all registered finders and return all results.
     *
     * @param file the jar file or sources root to search for classes
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllIn(File file) {
        return findAllIn(file, aClass -> true);
    }

    /**
     * Aggregates the find results of all finders registered in this provider.
     * <p>Use the predicate to filter the loaded classes before returning them.
     * <p>This will call the {@link Finder#findAllIn(ClassLoader, File)} method on all registered finders and return all results.
     *
     * @param file the jar file or sources root to search for classes
     * @param predicate the predicate to filter the loaded classes on
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllIn(File file, Predicate<Class<?>> predicate) {

        return findAllIn(configuration().classLoader(), file, predicate);
    }

    /**
     * Aggregates the find results of all finders registered in this provider.
     * <p>The provided class loader is used to load the found classes.
     * <p>Use the predicate to filter the loaded classes before returning them.
     * <p>This will call the {@link Finder#findAllIn(ClassLoader, File)} method on all registered finders and return all results.
     *
     * @param file the jar file or sources root to search for classes
     * @param predicate the predicate to filter the loaded classes on
     * @param classLoader the classloader used to load the classes inside the file
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllIn(ClassLoader classLoader, File file, Predicate<Class<?>> predicate) {

        return all().stream()
                .map(finder -> finder.findAllIn(classLoader, file, predicate))
                .collect(Collectors.toList());
    }

    /**
     * Aggregates the find results of all finders registered in this provider and calls the load method on all results.
     * <p>
     * This will call the {@link Finder#findAllIn(ClassLoader, File)} method on all registered finders
     * and then call {@link FinderResult#load(Scope)} on all of them returning the result.
     *
     * @param file the jar file or sources root to search for classes
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllAndLoadIn(File file) {
        return findAllAndLoadIn(file, aClass -> true);
    }

    /**
     * Aggregates the find results of all finders registered in this provider and calls the load method on all results.
     * <p>The loaded classes can be filtered using the predicate before the load method is called on them.
     * <p>This will call the {@link Finder#findAllIn(ClassLoader, File)} method on all registered finders
     * and then call {@link FinderResult#load(Scope)} on all of them returning the result.
     *
     * @param file the jar file or sources root to search for classes
     * @param predicate the predicate to filter the loaded classes on
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllAndLoadIn(File file, Predicate<Class<?>> predicate) {

        return findAllAndLoadIn(configuration().classLoader(), file, predicate);
    }

    /**
     * Aggregates the find results of all finders registered in this provider and calls the load method on all results.
     * <p>The provided classloader is used to load the classes inside the file.
     * <p>The loaded classes can be filtered using the predicate before the load method is called on them.
     * <p>This will call the {@link Finder#findAllIn(ClassLoader, File)} method on all registered finders
     * and then call {@link FinderResult#load(Scope)} on all of them returning the result.
     *
     * @param classLoader the classloader used to load the classes from the file
     * @param file the jar file or sources root to search for classes
     * @param predicate the predicate to filter the loaded classes on
     * @return a list of all finder results. this is never null but may be empty.
     */
    default Collection<FinderResult<?>> findAllAndLoadIn(ClassLoader classLoader, File file, Predicate<Class<?>> predicate) {

        Collection<FinderResult<?>> results = findAllIn(classLoader, file, predicate);
        results.forEach(result -> result.load(scope()));
        return results;
    }
}
