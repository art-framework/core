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

import io.artframework.impl.DefaultConfiguration;
import io.artframework.impl.DefaultExecutionContext;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Stack;

/**
 * The <pre>ExecutionContext</pre> holds a hierarchical order of execution
 * for all {@link Action}s, {@link Requirement}s and {@link Trigger} that were
 * involved in the execution chain of the root {@link ArtContext}.
 * <br>
 * The <pre>ExecutionContext</pre> is disposed once the last {@link ArtObject}
 * was executed or tested. You can use the {@link #data()} map to store data that is
 * shared among all {@link ArtObject}s involved in the execution of this context.
 * The data is only temporary and exists for the lifetime of this {@link Context}.
 *
 * @param <TContext> type of the context that is currently executing
 */
public interface ExecutionContext<TContext extends ArtObjectContext<?>> extends Context {

    /**
     * Creates a new {@link DefaultConfiguration} from the given parameters.
     *
     * @param configuration configuration of the context
     * @param rootContext The root context that initiated the execution. This can be null.
     * @param targets the targets that are used by this execution context
     * @return a new {@link ExecutionContext} for executing the ART
     */
    static ExecutionContext<?> of(
            @NonNull Configuration configuration,
            @Nullable Context rootContext,
            @NonNull Target<?>... targets
    ) {
        return new DefaultExecutionContext<>(configuration, rootContext, targets);
    }

    /**
     * Gets the root {@link Context} that initially triggered the execution
     * or test of this {@link ArtObject}.
     * The root context may not exist since {@link ArtObjectContext} objects
     * can be constructed any time and executed any time. It will exist if
     * this execution tree was initialized by an {@link ArtContext}.
     *
     * @return the {@link ArtContext} that initialized the execution tree.
     *          This may be empty if the execution was manually invoked.
     */
    Optional<Context> root();

    /**
     * Gets the parent of this {@link ExecutionContext} that
     * was directly executed before this {@link ArtObjectContext}.
     *
     * @return the parent that was executed before this context.
     *          May be empty if no parent exists.
     */
    Optional<ArtObjectContext<?>> parent();

    /**
     * Gets the full history of this {@link ExecutionContext} ordered
     * from newest to oldest {@link ArtObjectContext}.
     * This means the first item in the array (index 0) is the {@link #parent()}
     * of the {@link #current()} context.
     *
     * @return immutable execution history in a stack sorted format. From newest to oldest.
     */
    Collection<ArtObjectContext<?>> history();

    /**
     * Gets all targets that are linked to this execution context.
     * <p>
     * The execution context will check each target type against the next
     * executable context and use it if they match.
     *
     * @return an immutable list of targets in this execution context
     */
    Collection<Target<?>> targets();

    /**
     * Adds the given target to this execution context.
     *
     * @param target the target that should be added
     * @param <TTarget> type of the target that is added
     * @return this execution context
     */
    <TTarget> ExecutionContext<TContext> addTarget(Target<TTarget> target);

    /**
     * Gets the context that is currently being executed.
     * Can be null if the {@link ExecutionContext} was just constructed
     * and only contains a parent context.
     *
     * @return current {@link ArtObjectContext}
     */
    TContext current();

    /**
     * Stores a value for the {@link Target} of this {@link ExecutionContext} and the current {@link ArtObjectContext}.
     * This means a unique key is generated from the {@link Target#uniqueId()} and
     * {@link ArtObjectContext#uniqueId()} and will be appended by your key.
     * <br>
     * Then the {@link Storage#set(String, Object)} method is called and the data is persisted.
     * <br>
     * Use the {@link #data()} methods to store data that is only available in this scope
     * and not persisted to the database.
     *
     * @param target the target to store the value for
     * @param key      storage key
     * @param value    value to store
     * @param <TValue> type of the value
     * @return an {@link Optional} containing the existing value
     * @see Storage#set(String, Object)
     */
    <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull TValue value);

    /**
     * Retrieves a persistently stored value from the {@link Storage} and returns
     * it cast to the given type. Will return an empty {@link Optional} if casting
     * fails or the data does not exist.
     * <br>
     * The data that is fetched will be stored under a unique key combination of
     * {@link ArtObjectContext#uniqueId()} and {@link Target#uniqueId()}.
     * <br>
     * Use the {@link #data()} methods to store data that is only available in this scope
     * and not persisted to the database.
     *
     * @param target the target to get the value for
     * @param key      storage key
     * @param valueClass class of the value type you expect in return
     * @param <TValue> type of the value
     * @return the stored value or an empty {@link Optional} if the value type cannot be cast or does not exist
     */
    <TValue> Optional<TValue> store(@NonNull Target<?> target, @NonNull String key, @NonNull Class<TValue> valueClass);

    /**
     * Uses this {@link ExecutionContext} as a parent for the next {@link ArtObjectContext}
     * copying over all other parents and the root context of this execution context.
     *
     * @param nextContext the context that will be executed next
     * @param <TNextContext> type of the next context
     * @return the next execution context containing the properties of this context
     */
    <TNextContext extends ArtObjectContext<TArtObject>, TArtObject extends ArtObject> ExecutionContext<TNextContext> next(TNextContext nextContext);
}
