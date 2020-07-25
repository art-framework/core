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

package net.silthus.art;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class DefaultFutureResult implements FutureResult {

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private CombinedResult result;
    @Getter
    @Setter(AccessLevel.PRIVATE)
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
    public void onCompletion(Consumer<CombinedResult> callback) {
        if (isComplete()) {
            getConsumers().forEach(consumer -> consumer.accept(getResult()));
        } else {
            consumers.add(callback);
        }
    }

    @Override
    public CombinedResult complete(CombinedResult futureResult) {
        if (isComplete()) return getResult();

        setResult(combine(futureResult));
        getConsumers().forEach(consumer -> consumer.accept(getResult()));
        setComplete(true);

        return getResult();
    }

    @Override
    public FutureResult combine(Result result) {
        if (isComplete()) throw new UnsupportedOperationException("Cannot combine a completed FutureResult.");

        ArrayList<Consumer<CombinedResult>> consumers = new ArrayList<>(getConsumers());
        if (result instanceof FutureResult) {
            consumers.addAll(((FutureResult) result).getConsumers());
        }

        return new DefaultFutureResult(getResult().combine(result), consumers);
    }

    @Override
    public Collection<Consumer<CombinedResult>> getConsumers() {
        return ImmutableList.copyOf(consumers);
    }

    @Override
    public Collection<Result> getResults() {
        return getResult().getResults();
    }

    @Override
    public <TTarget> Collection<TargetResult<TTarget, ?>> getTargetResults(Target<TTarget> target) {
        return getResult().getTargetResults(target);
    }

    @Override
    public <TTarget> Collection<TargetResult<TTarget, ?>> getTargetResults(Class<TTarget> targetClass) {
        return getResult().getTargetResults(targetClass);
    }

    @Override
    public ResultStatus getStatus() {
        return getResult().getStatus();
    }

    @Override
    public String[] getMessages() {
        return getResult().getMessages();
    }
}
