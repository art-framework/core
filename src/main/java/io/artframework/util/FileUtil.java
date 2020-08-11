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

package io.artframework.util;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class FileUtil {

    @SuppressWarnings("unchecked")
    public static <T> List<Class<? extends T>> findClasses(
            @NonNull final ClassLoader classLoader,
            @NonNull final File file,
            @NonNull final Class<T> clazz
    ) {

        return findClasses(classLoader, file, clazz::isAssignableFrom).stream()
                .map(aClass -> (Class<T>) aClass.asSubclass(clazz))
                .collect(Collectors.toList());
    }

    public static List<Class<?>> findClasses(
            @NonNull final ClassLoader classLoader,
            @NonNull final File file,
            @NonNull final Predicate<Class<?>> predicate
            ) {

        if (!file.exists() || !file.getName().endsWith(".jar")) {
            return new ArrayList<>();
        }

        final List<Class<?>> classes = new ArrayList<>();

        try {

            final URL jar = file.toURI().toURL();

            final List<String> matches = new ArrayList<>();

            try (final JarInputStream stream = new JarInputStream(
                    jar.openStream()); final URLClassLoader loader = new URLClassLoader(new URL[]{jar},
                    classLoader)
            ) {
                JarEntry entry;
                while ((entry = stream.getNextJarEntry()) != null) {
                    final String name = entry.getName();
                    if (name == null || name.isEmpty() || !name.endsWith(".class")) {
                        continue;
                    }

                    matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
                }

                for (final String match : matches) {
                    try {
                        final Class<?> loaded = loader.loadClass(match);
                        if (predicate.test(loaded)) {
                            classes.add(loaded);
                        }
                    } catch (final NoClassDefFoundError ignored) {
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ImmutableList.copyOf(classes);
    }
}
