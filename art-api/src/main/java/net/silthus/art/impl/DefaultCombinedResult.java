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

package net.silthus.art.impl;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import net.silthus.art.*;

import java.util.*;
import java.util.stream.Collectors;

@Value
public class DefaultCombinedResult implements CombinedResult {

    @Getter(lazy = true)
    ResultStatus status = combineStatus();
    @Getter(lazy = true)
    String[] messages = combineMessages();
    @Singular
    List<Result> results;

    public DefaultCombinedResult() {
        this.results = new ArrayList<>();
    }

    public DefaultCombinedResult(List<Result> results) {
        this.results = ImmutableList.copyOf(results);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> Collection<TargetResult<TTarget, ?>> getTargetResults(Target<TTarget> target) {
        return getResults().stream()
                .filter(result -> result instanceof TargetResult)
                .filter(targetResult -> ((TargetResult<?, ?>) targetResult).getTarget().equals(target))
                .map(targetResult -> (TargetResult<TTarget, ?>) targetResult)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> Collection<TargetResult<TTarget, ?>> getTargetResults(Class<TTarget> targetClass) {
        return getResults().stream()
                .filter(result -> result instanceof TargetResult)
                .filter(targetResult -> targetClass.isInstance(((TargetResult<?, ?>) targetResult).getTarget().getSource()))
                .map(targetResult -> (TargetResult<TTarget, ?>) targetResult)
                .collect(Collectors.toList());
    }

    @Override
    public CombinedResult combine(Result result) {
        ArrayList<Result> results = new ArrayList<>(getResults());
        results.add(result);
        return new DefaultCombinedResult(results);
    }

    private String[] combineMessages() {
        return getResults().stream()
                .map(Result::getMessages)
                .reduce((list1, list2) -> {
                    HashSet<String> messages = new HashSet<>();
                    messages.addAll(Arrays.asList(list1));
                    messages.addAll(Arrays.asList(list2));
                    return messages.toArray(new String[0]);
                }).orElse(new String[0]);
    }

    private ResultStatus combineStatus() {
        return getResults().stream()
                .map(Result::getStatus)
                .reduce(ResultStatus::combine)
                .orElse(ResultStatus.EMPTY);
    }
}
