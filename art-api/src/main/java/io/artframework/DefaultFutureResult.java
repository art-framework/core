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

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@Accessors(fluent = true)
public final class DefaultFutureResult implements FutureResult {

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private CombinedResult result;
    @Getter
    private boolean complete;

    private final List<Consumer<CombinedResult>> consumers;

    public DefaultFutureResult() {
        this(CombinedResult.empty());
    }

    public DefaultFutureResult(@NonNull CombinedResult result) {
        this(result, new ArrayList<>());
    }

    DefaultFutureResult(@NonNull CombinedResult result, @NonNull Collection<Consumer<CombinedResult>> consumers) {
        this.result = result;
        this.consumers = new ArrayList<>(consumers);
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void onCompletion(Consumer<CombinedResult> callback) {
        if (isComplete()) {
            consumers().forEach(consumer -> consumer.accept(result()));
        } else {
            consumers.add(callback);
        }
    }

    @Override
    public CombinedResult complete() {
        if (isComplete()) return result();

        consumers().forEach(consumer -> consumer.accept(result()));
        complete = true;

        return result();
    }

    @Override
    public CombinedResult complete(Result futureResult) {
        if (isComplete()) return result();

        result(combine(futureResult));

        return complete();
    }

    @Override
    public FutureResult combine(Result result) {
        ArrayList<Consumer<CombinedResult>> consumers = new ArrayList<>(consumers());
        if (result instanceof FutureResult) {
            consumers.addAll(((FutureResult) result).consumers());
        }

        return new DefaultFutureResult(result().combine(result), consumers);
    }

    @Override
    public Collection<Consumer<CombinedResult>> consumers() {
        return ImmutableList.copyOf(consumers);
    }

    @Override
    public Collection<Result> results() {
        return result().results();
    }

    @Override
    public <TTarget> Collection<TargetResult<TTarget, ?, ?>> ofTarget(Target<TTarget> target) {
        return result().ofTarget(target);
    }

    @Override
    public <TTarget> Collection<TargetResult<TTarget, ?, ?>> ofTarget(Class<TTarget> targetClass) {
        return result().ofTarget(targetClass);
    }

    @Override
    public ResultStatus status() {
        return result().status();
    }

    @Override
    public String[] messages() {
        return result().messages();
    }
}
