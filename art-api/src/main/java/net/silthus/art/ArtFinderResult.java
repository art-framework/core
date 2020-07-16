package net.silthus.art;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Contains all of the classes that were found when searching the classpath
 * or a file for {@link ArtObject}s.
 * Classes that don't have an @{@link ArtOptions} annotation
 * or public parameterless constructor can be found in the
 */
public interface ArtFinderResult extends Iterable<ArtObjectInformation> {

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

    ArtFinderResult filter(Predicate<ArtObjectInformation<?>> predicate);

    Stream<ArtObjectInformation<?>> stream();

    /**
     * Returns a list of all classes excluding any classes that had errors.
     * Use the {@link #getErrors()} method to get all classes that had errors
     * while searching for {@link ArtObject}s.
     *
     * @return a list of all classes found by the {@link ArtFinder}
     */
    Collection<ArtObjectInformation<?>> getAll();

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
