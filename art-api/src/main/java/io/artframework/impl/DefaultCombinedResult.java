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

package io.artframework.impl;

import com.google.common.collect.ImmutableList;
import io.artframework.*;
import io.artframework.util.ReflectionUtil;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

@Value
@Accessors(fluent = true)
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
        this.results = ImmutableList.copyOf(flatten(results));
    }

    private Collection<Result> flatten(Collection<Result> results) {
        ArrayList<Result> flatResults = new ArrayList<>();
        for (Result result : results) {
            if (result instanceof CombinedResult) {
                flatResults.addAll(flatten(((CombinedResult) result).results()));
            } else {
                flatResults.add(result);
            }
        }
        return flatResults;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> Collection<TargetResult<TTarget, ?, ?>> ofTarget(Target<TTarget> target) {
        return results().stream()
                .filter(result -> result instanceof TargetResult)
                .filter(targetResult -> ((TargetResult<?, ?, ?>) targetResult).target().equals(target))
                .map(targetResult -> (TargetResult<TTarget, ?, ?>) targetResult)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TTarget> Collection<TargetResult<TTarget, ?, ?>> ofTarget(Class<TTarget> targetClass) {
        return results().stream()
                .filter(result -> result instanceof TargetResult)
                .filter(targetResult -> ReflectionUtil.isTargetType(targetClass, ((TargetResult<?, ?, ?>) targetResult).target()))
                .map(targetResult -> (TargetResult<TTarget, ?, ?>) targetResult)
                .collect(Collectors.toList());
    }

    @Override
    public CombinedResult combine(Result result) {
        ArrayList<Result> results = new ArrayList<>(results());
        results.add(result);
        return new DefaultCombinedResult(results);
    }

    private String[] combineMessages() {
        return results().stream()
                .map(Result::messages)
                .reduce((list1, list2) -> {
                    HashSet<String> messages = new HashSet<>();
                    messages.addAll(Arrays.asList(list1));
                    messages.addAll(Arrays.asList(list2));
                    return messages.toArray(new String[0]);
                }).orElse(new String[0]);
    }

    private ResultStatus combineStatus() {
        return results().stream()
                .map(Result::status)
                .reduce(ResultStatus::combine)
                .orElse(ResultStatus.EMPTY);
    }
}
