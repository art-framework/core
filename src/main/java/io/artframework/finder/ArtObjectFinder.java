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
import io.artframework.annotations.ART;
import io.artframework.util.FileUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ArtObjectFinder extends AbstractFinder {

    public ArtObjectFinder(Scope scope) {
        super(scope);
    }

    @Override
    protected ArtObjectFinderResult findAllIn(File... files) {

        final List<ArtObjectMeta<?>> artObjectMetas = new ArrayList<>();
        final List<ArtObjectError> errors = new ArrayList<>();

        Arrays.stream(files)
                .flatMap(file -> FileUtil.findClasses(configuration().classLoader(), file, ArtObject.class).stream())
                .forEach(artClass -> {
                    if (Trigger.class.isAssignableFrom(artClass)) {
                        for (Method method : artClass.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(ART.class)) {
                                try {
                                    artObjectMetas.add(ArtObjectMeta.of(artClass, method));
                                } catch (ArtMetaDataException e) {
                                    errors.add(e.error());
                                }
                            }
                        }
                    } else {
                        try {
                            artObjectMetas.add(ArtObjectMeta.of(artClass));
                        } catch (ArtMetaDataException e) {
                            errors.add(e.error());
                        }
                    }
                });

        return new ArtObjectFinderResult(artObjectMetas, errors);
    }

    public static final class ArtObjectFinderResult extends AbstractFinderResult<ArtObjectMeta<?>> {

        public static ArtObjectFinderResult empty() {
            return new ArtObjectFinderResult(new ArrayList<>(), new ArrayList<>());
        }

        public ArtObjectFinderResult(Collection<ArtObjectMeta<?>> artObjectMetas, Collection<ArtObjectError> artObjectErrors) {
            super(artObjectMetas, artObjectErrors);
        }

        @Override
        public FinderResult<ArtObjectMeta<?>> load(Configuration configuration) {

            configuration.art().addAll(results());
            return this;
        }
    }
}
