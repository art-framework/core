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

package io.artframework.finder;

import com.google.common.collect.ImmutableList;
import io.artframework.ArtObjectError;
import io.artframework.FinderResult;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

@Getter
@Accessors(fluent = true)
public abstract class AbstractFinderResult<TResult> implements FinderResult<TResult> {

    private final Collection<TResult> results;
    private final Collection<ArtObjectError> errors;

    protected AbstractFinderResult(Collection<TResult> results, Collection<ArtObjectError> errors) {
        this.results = ImmutableList.copyOf(results);
        this.errors = ImmutableList.copyOf(errors);
    }

    @Override
    public Collection<TResult> results() {
        return results;
    }

    @Override
    public Collection<ArtObjectError> errors() {
        return errors;
    }

    @Override
    public final FinderResult<TResult> forEachResult(Consumer<TResult> consumer) {
        results().forEach(consumer);
        return this;
    }

    @Override
    public final FinderResult<TResult> forEachError(Consumer<ArtObjectError> consumer) {
        errors().forEach(consumer);
        return this;
    }

    @Override
    public final Iterator<TResult> iterator() {
        return results().iterator();
    }
}
