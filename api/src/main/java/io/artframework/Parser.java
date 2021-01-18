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

/**
 * The parser parses the given input into a valid art context.
 * <p>The art-framework ships with one default parser, the {@link FlowParser}.
 * <p>All operations inside a parser should be atomic without side effects.
 *
 * @param <TInput> the input type of the parser
 */
public interface Parser<TInput> extends Scoped {

    /**
     * Returns the storage key set by the {@link #storageKey(String)} method
     * or the random pre generated storage key.
     *
     * @return the configured storage key of this parser
     */
    String storageKey();

    /**
     * Sets a static storage key for all art objects created by this parser.
     * <p>This is needed if the created {@link ArtObjectContext} should persistently
     * store the data under the given storage key.
     * <p>Make sure you create separate parser instances for each storage key.
     *
     * @param key the key to set as storage key
     * @return this parser
     */
    Parser<TInput> storageKey(String key);

    /**
     * Parses the given input into an {@link ArtContext}.
     *
     * @param input the input that should be parses. must not be null.
     * @return the resulting art context
     * @throws ParseException if the parse operation fails, e.g. when the syntax is incorrect.
     */
    ArtContext parse(@NonNull TInput input) throws ParseException;
}
