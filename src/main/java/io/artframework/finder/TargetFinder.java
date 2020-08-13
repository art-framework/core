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

import io.artframework.*;
import io.artframework.util.FileUtil;
import io.artframework.util.ReflectionUtil;
import lombok.Value;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class TargetFinder extends AbstractFinder {

    public TargetFinder(Scope scope) {
        super(scope);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TargetFinderResult findAllIn(File file, Predicate<Class<?>> predicate) {

        final Collection<TargetClassWrapper<?>> targets = new ArrayList<>();
        final Collection<ArtObjectError> errors = new ArrayList<>();

        FileUtil.findClasses(configuration().classLoader(), file, Target.class)
                .stream().filter(predicate)
                .forEach(targetClass -> {
                    // TODO: fix type argument inheritance
                    // the util method only works with direct interfaces
                    // but not with abstract classes that pass the type to the implementing class
                    Optional<Class<?>> sourceClass = ReflectionUtil.getInterfaceTypeArgument(targetClass, Target.class, 0);
                    if (sourceClass.isPresent()) {
                        try {
                            Constructor<? extends Target> constructor = targetClass.getDeclaredConstructor(sourceClass.get());
                            constructor.setAccessible(true);
                            targets.add(new TargetClassWrapper(sourceClass.get(), constructor));
                        } catch (NoSuchMethodException e) {
                            errors.add(ArtObjectError.of(ArtObjectError.Reason.INVALID_CONSTRUCTOR, targetClass, e));
                        }
                    } else {
                        errors.add(ArtObjectError.of("Unable to determine source type of the target class " + targetClass.getCanonicalName(),
                                ArtObjectError.Reason.INVALID_ART_OBJECT,
                                targetClass));
                    }
                });

        return new TargetFinderResult(targets, errors);
    }

    public static final class TargetFinderResult extends AbstractFinderResult<TargetClassWrapper<?>> {

        private TargetFinderResult(Collection<TargetClassWrapper<?>> targetClassWrappers, Collection<ArtObjectError> exceptions) {
            super(targetClassWrappers, exceptions);
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public FinderResult<TargetClassWrapper<?>> load(Scope scope) {
            for (final TargetClassWrapper<?> result : results()) {
                scope.configuration().targets().add(result.targetClass, o -> {
                    try {
                        return (Target) result.constructor.newInstance(o);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        scope.configuration().targets().remove(result.targetClass);
                        throw new RuntimeException(e);
                    }
                });
            }

            return this;
        }
    }

    @Value
    public static class TargetClassWrapper<TTarget> {

        Class<TTarget> targetClass;
        Constructor<Target<TTarget>> constructor;
    }
}
