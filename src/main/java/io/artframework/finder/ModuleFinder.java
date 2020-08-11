/*
 *  Copyright 2020 ART-Framework Contributors (https://github.com/art-framework/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.artframework.finder;

import io.artframework.AbstractFinder;
import io.artframework.ArtObjectError;
import io.artframework.Configuration;
import io.artframework.FinderResult;
import io.artframework.ModuleRegistrationException;
import io.artframework.annotations.ArtModule;
import io.artframework.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ModuleFinder extends AbstractFinder {

    public ModuleFinder(Configuration configuration) {

        super(configuration);
    }

    @Override
    protected FinderResult<?> findAllIn(File... files) {

        final List<Class<?>> moduleClasses = new ArrayList<>();
        final List<ArtObjectError> errors = new ArrayList<>();

        Arrays.stream(files)
                .flatMap(file -> FileUtil.findClasses(configuration().classLoader(), file, aClass -> aClass.isAnnotationPresent(ArtModule.class)).stream())
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
        public FinderResult<Class<?>> load(Configuration configuration) {

            for (Class<?> result : results()) {
                try {
                    configuration.modules().enable(result);
                } catch (ModuleRegistrationException e) {
                    e.printStackTrace();
                }
            }

            return this;
        }
    }

}
