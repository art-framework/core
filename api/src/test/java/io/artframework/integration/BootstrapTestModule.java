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

import io.artframework.Module;
import io.artframework.*;
import io.artframework.annotations.ArtModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@ArtModule("test")
public class BootstrapTestModule implements BootstrapModule {

    @ArtModule("bootstrap")
    public static class BootstrapModule implements Module {

        public static boolean called = false;

        public void onBootstrap(BootstrapScope scope) {
            called = true;
            scope.addProvider(CustomProvider.class, CustomProvider::new);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class CustomProvider implements Provider {

        private final Scope scope;

        public CustomProvider(Scope scope) {

            this.scope = scope;
        }
    }

    @ArtModule("load")
    public static class LoadModule implements Module {

        public static boolean called = false;

        @Override
        public void onLoad(Scope scope) throws Exception {
            called = true;
        }
    }

    @ArtModule("enable")
    public static class EnableModule implements Module {

        public static boolean called = false;

        @Override
        public void onEnable(Scope scope) throws Exception {
            called = true;
        }
    }

    @ArtModule("error-bootstrap")
    public static class ErrorBootstrapModule implements Module {

        public static boolean called = false;

        @Override
        public void onBootstrap(BootstrapScope scope) throws Exception {
            throw new Exception("error");
        }

        @Override
        public void onLoad(Scope scope) {
            called = true;
        }
    }

    @ArtModule("error-load")
    public static class ErrorLoadModule implements Module {

        public static boolean called = false;

        @Override
        public void onLoad(Scope scope) throws Exception {
            throw new Exception("error");
        }

        @Override
        public void onEnable(Scope scope) {
            called = true;
        }
    }

    @ArtModule("error-enable")
    public static class ErrorEnableModule implements Module {

        @Override
        public void onEnable(Scope scope) throws Exception {
            throw new Exception("error");
        }
    }
}
