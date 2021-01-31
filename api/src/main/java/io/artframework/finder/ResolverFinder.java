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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResolverFinder extends AbstractFinder {

    public ResolverFinder(Scope scope) {
        super(scope);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResolverFinderResult findAllIn(ClassLoader classLoader, File file, Predicate<Class<?>> predicate) {

        return new ResolverFinderResult(FileUtil.findClasses(classLoader, file, Resolver.class)
                .stream().filter(predicate)
                .filter(this::search)
                .collect(Collectors.toList()));
    }

    public static final class ResolverFinderResult extends AbstractFinderResult<Class<? extends Resolver<?>>> {

        private ResolverFinderResult(List<Class<? extends Resolver<?>>> resolverClassWrappers) {
            super(resolverClassWrappers, new ArrayList<>());
        }

        @Override
        public FinderResult<Class<? extends Resolver<?>>> load(Scope scope) {
            for (Class<? extends Resolver<?>> result : results()) {
                scope.configuration().resolvers().add(result);
            }

            return this;
        }
    }
}
