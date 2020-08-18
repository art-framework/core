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
import io.artframework.annotations.Module;
import io.artframework.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class ModuleFinder extends AbstractFinder {

    public ModuleFinder(Scope scope) {

        super(scope);
    }

    @Override
    public FinderResult<?> findAllIn(File file, Predicate<Class<?>> predicate) {

        final List<Class<?>> moduleClasses = new ArrayList<>();
        final List<ArtObjectError> errors = new ArrayList<>();

        FileUtil.findClasses(configuration().classLoader(), file, aClass -> aClass.isAnnotationPresent(Module.class))
                .stream().filter(predicate)
                .forEach(moduleClass -> {
            try {
                configuration().modules().register(moduleClass);
                moduleClasses.add(moduleClass);
            } catch (ModuleRegistrationException e) {
                errors.add(ArtObjectError.of(moduleClass, e));
            }
        });

        return new ModuleFinderResult(moduleClasses, errors);
    }

    public static final class ModuleFinderResult extends AbstractFinderResult<Class<?>> {

        private ModuleFinderResult(Collection<Class<?>> classes, Collection<ArtObjectError> errors) {

            super(classes, errors);
        }

        @Override
        public FinderResult<Class<?>> load(Scope scope) {

            for (Class<?> result : results()) {
                try {
                    scope.configuration().modules().enable(result);
                } catch (ModuleRegistrationException e) {
                    e.printStackTrace();
                }
            }

            return this;
        }
    }

}
