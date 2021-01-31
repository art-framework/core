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

package io.artframework;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFinder extends AbstractScoped implements Finder {

    private final Set<Class<?>> searchedClasses = new HashSet<>();

    protected AbstractFinder(Scope scope) {
        super(scope);
    }

    /**
     * Checks if the given class was already searched by this finder.
     *
     * @param clazz the class to search
     * @return true if the class was not searched and false if it was already visited
     */
    protected boolean search(Class<?> clazz) {

        if (searchedClasses.contains(clazz)) return false;
        searchedClasses.add(clazz);
        return true;
    }
}
