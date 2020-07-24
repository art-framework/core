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

package net.silthus.art.events;

import lombok.Data;
import lombok.NonNull;
import net.silthus.art.ResultStatus;
import net.silthus.art.Target;
import net.silthus.art.TargetedTestResult;
import net.silthus.art.TestResult;

import javax.annotation.Nullable;
import java.util.*;

@Data
public class DefaultTestResult implements TestResult {

    private final ResultStatus result;
    private final Set<String> failureReasons = new HashSet<>();
    private final Set<String> errorReasons = new HashSet<>();
    private final Map<Target<?>, ResultStatus> results = new HashMap<>();

    public DefaultTestResult(@NonNull ResultStatus result) {
        this(result, new String[0], new String[0]);
    }

    public DefaultTestResult(@NonNull ResultStatus result, @NonNull Target<?> target) {
        this(result);
        results.put(target, result);
    }

    public DefaultTestResult(@NonNull ResultStatus result, @Nullable String[] failureReasons, @Nullable String[] errorReasons) {
        this.result = result;
        if (failureReasons != null) this.failureReasons.addAll(Arrays.asList(failureReasons));
        if (errorReasons != null) this.errorReasons.addAll(Arrays.asList(errorReasons));
    }

    public DefaultTestResult(@NonNull ResultStatus result, @NonNull Target<?> target, @Nullable String[] failureReasons, @Nullable String[] errorReasons) {
        this(result, failureReasons, errorReasons);
        results.put(target, result);
    }

    @Override
    public boolean isSuccessful() {
        return getResult() == ResultStatus.SUCCESS || getResult() == ResultStatus.EMPTY;
    }

    @Override
    public boolean hasError() {
        return getResult() == ResultStatus.ERROR;
    }

    @Override
    public <TTarget> Optional<TargetedTestResult<TTarget>> forTarget(Target<TTarget> target) {
        return Optional.ofNullable(getResults().get(target))
                .map(result -> TargetedTestResult.of(target, result));
    }

    @Override
    public <TTarget> Optional<TargetedTestResult<TTarget>> forTarget(Class<TTarget> targetClass) {
        return getResults().entrySet().stream()
                .filter(targetResultEntry -> targetResultEntry.getKey().isTargetType(targetClass))
                .map(Map.Entry::getValue)
                .reduce(ResultStatus::combine)
                .map(result -> TargetedTestResult.of(targetClass, result));
    }

    @Override
    public TestResult combine(@NonNull TestResult result) {

        DefaultTestResult testResult = new DefaultTestResult(getResult().combine(result.getResult()));
        testResult.getResults().putAll(getResults());
        testResult.getResults().putAll(result.getResults());
        testResult.getFailureReasons().addAll(getFailureReasons());
        testResult.getFailureReasons().addAll(result.getFailureReasons());
        testResult.getErrorReasons().addAll(getErrorReasons());
        testResult.getErrorReasons().addAll(result.getErrorReasons());

        return testResult;
    }

    @Override
    public <TTarget> TargetedTestResult<TTarget> addTarget(@NonNull Target<TTarget> target) {

        TargetedTestResult<TTarget> testResult = TargetedTestResult.of(
                target,
                getResult()
        );

        testResult.getResults().putAll(getResults());
        testResult.getFailureReasons().addAll(getFailureReasons());
        testResult.getErrorReasons().addAll(getErrorReasons());

        return testResult;
    }
}
