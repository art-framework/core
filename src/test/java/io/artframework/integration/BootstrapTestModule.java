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

package io.artframework.integration;

import io.artframework.*;
import io.artframework.annotations.ArtModule;

import java.util.ArrayList;
import java.util.Collection;

@ArtModule("test")
public class BootstrapTestModule implements BootstrapModule {

    public BootstrapTestModule() throws BootstrapException {
        ART.bootstrap(this);
    }

    @Override
    public Collection<Object> modules() {
        return new ArrayList<>();
    }

    @Override
    public void enable(BootstrapScope scope) {

    }

    @Override
    public void disable(Scope scope) {

    }
}
