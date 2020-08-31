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

package io.artframework.impl;

import io.artframework.AbstractProvider;
import io.artframework.InjectionProvider;
import io.artframework.Scope;

import java.lang.reflect.Constructor;

import static io.artframework.util.ConfigUtil.loadConfigFields;

public class DefaultInjectionProvider extends AbstractProvider implements InjectionProvider {

    public DefaultInjectionProvider(Scope scope) {
        super(scope);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TObject> TObject create(Class<TObject> objectClass, Scope scope) throws ReflectiveOperationException {

        TObject object = null;
        for (Constructor<?> constructor : objectClass.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            if (constructor.getParameterTypes().length == 1 && Scope.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                object = (TObject) constructor.newInstance(scope);
                break;
            } else if (constructor.getParameterTypes().length < 1) {
                object = (TObject) constructor.newInstance();
                break;
            }
        }

        if (object == null) {
            throw new ReflectiveOperationException("Unable to find a valid constructor to create a new instance of " + objectClass.getCanonicalName());
        }

        loadConfigFields(scope, object);

        return object;
    }
}
