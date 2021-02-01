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

import io.artframework.AbstractFinder;
import io.artframework.ArtMetaDataException;
import io.artframework.ArtObject;
import io.artframework.ArtObjectError;
import io.artframework.ArtObjectMeta;
import io.artframework.FinderResult;
import io.artframework.Scope;
import io.artframework.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ArtObjectFinder extends AbstractFinder {

    public ArtObjectFinder(Scope scope) {
        super(scope);
    }

    @Override
    public FinderResult<?> findAllIn(ClassLoader classLoader, File file, Predicate<Class<?>> predicate) {

        final List<ArtObjectMeta<?>> artObjectMetas = new ArrayList<>();
        final List<ArtObjectError> errors = new ArrayList<>();

        FileUtil.findClasses(classLoader, file, ArtObject.class)
                .stream().filter(predicate)
                .filter(this::search)
                .forEach(artClass -> {
                    try {
                        artObjectMetas.add(ArtObjectMeta.of(scope(), artClass));
                    } catch (ArtMetaDataException e) {
                        errors.add(e.error());
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
        public FinderResult<ArtObjectMeta<?>> load(Scope scope) {

            scope.configuration().art().addAll(results().stream()
                    .filter(ArtObjectMeta::autoRegister)
                    .collect(Collectors.toList()));
            return this;
        }
    }
}
