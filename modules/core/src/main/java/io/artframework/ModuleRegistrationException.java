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

public class ModuleRegistrationException extends ModuleException {

    private final ModuleState state;

    public ModuleRegistrationException(ModuleMeta moduleMeta, ModuleState state) {

        super(moduleMeta);
        this.state = state;
    }

    public ModuleRegistrationException(ModuleMeta moduleMeta, ModuleState state, String message) {

        super(moduleMeta, message);
        this.state = state;
    }

    public ModuleRegistrationException(ModuleMeta moduleMeta, ModuleState state, String message, Throwable cause) {

        super(moduleMeta, message, cause);
        this.state = state;
    }

    public ModuleRegistrationException(ModuleMeta moduleMeta, ModuleState state, Throwable cause) {

        super(moduleMeta, cause);
        this.state = state;
    }
}
