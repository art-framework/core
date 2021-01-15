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

import io.artframework.annotations.Config;
import io.artframework.impl.DefaultInjectionProvider;

/**
 * The injection provider provides the possibility to create objects from classes.
 * <p>
 * It forms an abstraction layer for the dependency injection used in the art-framework.
 * Replace it with the implementing dependency provider to create objects.
 */
public interface InjectionProvider {

    static InjectionProvider of(Scope scope) {
        return new DefaultInjectionProvider(scope);
    }

    /**
     * Tries to create a new instance of the given class and uses the scope to
     * provide any required dependencies.
     * <p>
     * It will also try to inject any fields that are marked as to inject, e.g. a @{@link Config} field.
     *
     * @param objectClass the class to create a new instance from
     * @param scope the scope that should be used by the instance
     * @param <TObject> the type of the object
     * @return the created object
     * @throws ReflectiveOperationException if an error occurred while creating an instance of the given class
     */
    <TObject> TObject create(Class<TObject> objectClass, Scope scope) throws ReflectiveOperationException;
}
