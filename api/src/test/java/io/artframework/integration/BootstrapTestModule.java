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

import io.artframework.BootstrapModule;
import io.artframework.BootstrapScope;
import io.artframework.Provider;
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;
import io.artframework.annotations.OnBootstrap;
import io.artframework.annotations.OnEnable;
import io.artframework.annotations.OnLoad;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ArtModule("test")
public class BootstrapTestModule implements BootstrapModule {

    @Override
    public Collection<Object> modules(BootstrapScope scope) {

        List<Object> objects = new ArrayList<>();
        objects.add(BootstrapModule.class);
        objects.add(new LoadModule());
        objects.add(EnableModule.class);
        return objects;
    }

    @ArtModule("bootstrap")
    public static class BootstrapModule {

        public static boolean called = false;

        @OnBootstrap
        public void onBootstrap(BootstrapScope scope) {
            called = true;
            scope.add(CustomProvider.class, CustomProvider::new);
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
    public static class LoadModule {

        public static boolean called = false;

        @OnLoad
        public void onLoad() {
            called = true;
        }
    }

    @ArtModule("enable")
    public static class EnableModule {

        public static boolean called = false;

        @OnEnable
        public void onEnable() {
            called = true;
        }
    }

    @ArtModule("error-bootstrap")
    public static class ErrorBootstrapModule {

        public static boolean called = false;

        @OnBootstrap
        public void onBootstrap() throws Exception {
            throw new Exception("error");
        }

        @OnLoad
        public void onLoad() {
            called = true;
        }
    }

    @ArtModule("error-load")
    public static class ErrorLoadModule {

        public static boolean called = false;

        @OnLoad
        public void onLoad() throws Exception {
            throw new Exception("error");
        }

        @OnEnable
        public void onEnable() {
            called = true;
        }
    }

    @ArtModule("error-enable")
    public static class ErrorEnableModule {

        @OnEnable
        public void onEnable() throws Exception {
            throw new Exception("error");
        }
    }
}
