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

public class ReplacementFinder extends AbstractFinder {

    public ReplacementFinder(Scope scope) {
        super(scope);
    }

    @Override
    public ReplacementFinderResult findAllIn(ClassLoader classLoader, File file, Predicate<Class<?>> predicate) {

        final Collection<ReplacementClassWrapper> replacements = new ArrayList<>();
        final Collection<ArtObjectError> errors = new ArrayList<>();

        FileUtil.findClasses(classLoader, file, Replacement.class)
                .stream().filter(predicate)
                .filter(this::search)
                .forEach(clazz -> {
                    try {
                        Constructor<? extends Replacement> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        replacements.add(new ReplacementClassWrapper(clazz, constructor));
                    } catch (NoSuchMethodException e) {
                        errors.add(ArtObjectError.of(ArtObjectError.Reason.INVALID_CONSTRUCTOR, clazz, e));
                    }
                });

        return new ReplacementFinderResult(replacements, errors);
    }

    public static final class ReplacementFinderResult extends AbstractFinderResult<ReplacementClassWrapper> {

        private ReplacementFinderResult(Collection<ReplacementClassWrapper> replacementClassWrappers, Collection<ArtObjectError> exceptions) {
            super(replacementClassWrappers, exceptions);
        }

        @Override
        public FinderResult<ReplacementClassWrapper> load(Scope scope) {
            for (final ReplacementClassWrapper result : results()) {
                try {
                    scope.configuration().replacements().add(result.getConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            return this;
        }
    }

    @Value
    public static class ReplacementClassWrapper {

        Class<? extends Replacement> replacementClass;
        Constructor<? extends Replacement> constructor;
    }
}
