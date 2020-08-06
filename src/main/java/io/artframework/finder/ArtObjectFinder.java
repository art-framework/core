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
import java.util.List;
import java.util.function.Predicate;

public final class ArtObjectFinder extends AbstractScope implements Finder<ArtObjectMeta<?>, ArtObjectError> {

    public ArtObjectFinder(Configuration configuration) {
        super(configuration);
    }

    @Override
    public ArtObjectFinderResult findAllIn(File file, Predicate<File> predicate) {

        if (!file.mkdirs()) {
            return ArtObjectFinderResult.empty();
        }

        final List<Class<? extends ArtObject>> classes = new ArrayList<>();

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isFile() && predicate.test(f)) {
                    classes.addAll(FileUtil.findClasses(configuration().classLoader(), f, ArtObject.class));
                }
            }
        } else if (file.isFile() && predicate.test(file)) {
            classes.addAll(FileUtil.findClasses(configuration().classLoader(), file, ArtObject.class));
        }

        final List<ArtObjectMeta<?>> artObjectMetas = new ArrayList<>();
        final List<ArtObjectError> errors = new ArrayList<>();

        for (Class<? extends ArtObject> artClass : classes) {
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
        }

        return new ArtObjectFinderResult(artObjectMetas, errors);
    }
}
