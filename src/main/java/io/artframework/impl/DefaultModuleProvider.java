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

import io.artframework.*;
import io.artframework.annotations.OnDisable;
import io.artframework.annotations.OnEnable;
import io.artframework.annotations.OnLoad;
import io.artframework.events.ModuleDisabledEvent;
import io.artframework.events.ModuleEnabledEvent;
import io.artframework.events.ModuleRegisteredEvent;
import io.artframework.util.graphs.CycleSearch;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

public class DefaultModuleProvider extends AbstractProvider implements ModuleProvider {

    final Map<Class<?>, ModuleInformation> modules = new HashMap<>();
    private CycleSearch<ModuleMeta> cycleSearcher = new CycleSearch<>(new boolean[0][0], new ModuleMeta[0]);
    private ArtModuleDependencyResolver resolver;

    public DefaultModuleProvider(@NonNull Scope scope) {
        super(scope);
    }

    @Override
    public ModuleProvider resolver(@Nullable ArtModuleDependencyResolver resolver) {
        this.resolver = resolver;
        return this;
    }

    @Override
    public Optional<ArtModuleDependencyResolver> resolver() {
        return Optional.ofNullable(resolver);
    }

    @Override
    public ModuleProvider register(@NonNull Object module) throws ModuleRegistrationException {

        registerModule(module);

        return this;
    }

    @Override
    public ModuleProvider register(@NonNull Class<?> moduleClass) throws ModuleRegistrationException {

        registerModule(moduleClass);

        return this;
    }

    public ModuleProvider enable(@NonNull Object module) throws ModuleRegistrationException {

        enable(registerModule(module));

        return this;
    }

    @Override
    public ModuleProvider enable(@NonNull Class<?> moduleClass) throws ModuleRegistrationException {

        enable(registerModule(moduleClass));

        return this;
    }

    @Override
    public ModuleProvider disable(@NonNull Object module) {

        ModuleInformation information = modules.remove(module.getClass());
        if (information != null) {
            disable(information);
        }

        return this;
    }

    private ModuleInformation registerModule(Class<?> moduleClass) throws ModuleRegistrationException {

        try {
            ModuleMeta moduleMeta = ModuleMeta.of(moduleClass);

            try {
                Constructor<?> constructor = moduleClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object artModule = constructor.newInstance();
                return registerModule(moduleMeta, artModule);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ModuleRegistrationException(
                        moduleMeta,
                        ModuleState.INVALID_MODULE,
                        "Unable to create a new instance of the ArtModule " + moduleClass.getSimpleName() + ". " +
                                "Does it have a parameterless public constructor?",
                        e
                );
            }
        } catch (ArtMetaDataException e) {
            throw new ModuleRegistrationException(null, ModuleState.INVALID_MODULE, e);
        }
    }

    private ModuleInformation registerModule(Object module) throws ModuleRegistrationException {

        try {
            return registerModule(ModuleMeta.of(module.getClass()), module);
        } catch (ArtMetaDataException e) {
            throw new ModuleRegistrationException(null, ModuleState.INVALID_MODULE, e);
        }
    }

    private ModuleInformation registerModule(ModuleMeta moduleMeta, @Nullable Object module) throws ModuleRegistrationException {
        Optional<ModuleMeta> existingModule = modules.values().stream().map(ModuleInformation::moduleMeta)
                .filter(meta -> meta.identifier().equals(moduleMeta.identifier()) && !meta.moduleClass().equals(moduleMeta.moduleClass())).findAny();
        if (existingModule.isPresent()) {
            throw new ModuleRegistrationException(moduleMeta, ModuleState.DUPLICATE_MODULE,
                    "There is already a module named \"" + moduleMeta.identifier() + "\" registered: " + existingModule.get().moduleClass().getCanonicalName());
        }

        ModuleInformation moduleInformation;
        if (modules.containsKey(moduleMeta.moduleClass())) {
            moduleInformation = this.modules.get(moduleMeta.moduleClass());
        } else {
            moduleInformation = updateModuleCache(new ModuleInformation(moduleMeta, module).state(ModuleState.REGISTERED));
            ART.callEvent(new ModuleRegisteredEvent(moduleMeta));
            cycleSearcher = CycleSearch.of(modules.values().stream().map(ModuleInformation::moduleMeta).collect(Collectors.toList()));

            Optional<List<ModuleMeta>> dependencyGraph = getDependencyGraph(moduleInformation);
            if (dependencyGraph.isPresent()) {
                updateModuleCache(moduleInformation.state(ModuleState.CYCLIC_DEPENDENCIES));
                throw new ModuleRegistrationException(moduleInformation.moduleMeta(), moduleInformation.state(),
                        "The module \"" + moduleInformation.moduleMeta().identifier() + "\" has cyclic dependencies: " + dependencyGraphToString(dependencyGraph.get()));
            }
        }

        return moduleInformation;
    }

    private void enable(ModuleInformation moduleInformation) throws ModuleRegistrationException {

        if (moduleInformation.state() == ModuleState.ENABLED) return;

        if (hasMissingDependencies(moduleInformation)) {
            updateModuleCache(moduleInformation.state(ModuleState.MISSING_DEPENDENCIES));
            throw new ModuleRegistrationException(moduleInformation.moduleMeta(), moduleInformation.state(),
                    "The module \"" + moduleInformation.moduleMeta().identifier() + "\" is missing the following dependencies: " + String.join(",", getMissingDependencies(moduleInformation)));
        }

        for (ModuleInformation childModule : getModules(moduleInformation.moduleMeta().dependencies())) {
            try {
                enable(childModule);
            } catch (ModuleRegistrationException e) {
                updateModuleCache(moduleInformation.state(ModuleState.DEPENDENCY_ERROR));
                throw new ModuleRegistrationException(moduleInformation.moduleMeta(), ModuleState.DEPENDENCY_ERROR,
                        "Failed to enable the module \"" + moduleInformation.moduleMeta().identifier() + "\" because a child module could not be enabled: " + e.getMessage(), e);
            }
        }

        try {
            moduleInformation.onEnable(configuration());
            updateModuleCache(moduleInformation.state(ModuleState.ENABLED));
            ART.callEvent(new ModuleEnabledEvent(moduleInformation.moduleMeta()));
        } catch (Exception e) {
            updateModuleCache(moduleInformation.state(ModuleState.ERROR));
            throw new ModuleRegistrationException(moduleInformation.moduleMeta(), ModuleState.ERROR,
                    "An error occured when trying to enable the module \"" + moduleInformation.moduleMeta().identifier() + "\": " + e.getMessage(), e);
        }
    }

    private void disable(ModuleInformation module) {

        module.onDisable(configuration());
        ART.callEvent(new ModuleDisabledEvent(module.moduleMeta()));
    }

    private Optional<List<ModuleMeta>> getDependencyGraph(ModuleInformation information) {

        return this.cycleSearcher.getCycles().stream()
                .filter(moduleMetas -> !moduleMetas.isEmpty())
                .filter(moduleMetas -> moduleMetas.get(0).equals(information.moduleMeta()))
                .findFirst();
    }

    private String dependencyGraphToString(@NonNull List<ModuleMeta> graph) {

        ModuleMeta sourceModule = graph.get(0);
        if (graph.size() < 2) return sourceModule.identifier() + " depends on itself!";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < graph.size(); i++) {
            sb.append(graph.get(i).identifier()).append(" --> ");

            if (i == graph.size() -1) {
                sb.append(sourceModule.identifier());
            }
        }

        return sb.toString();
    }

    private boolean hasCyclicDependencies(ModuleInformation moduleInformation) {

        return getDependencyGraph(moduleInformation).isPresent();
    }

    private Collection<String> getMissingDependencies(ModuleInformation moduleInformation) {

        String[] dependencies = moduleInformation.moduleMeta().dependencies();

        if (dependencies.length < 1) return new ArrayList<>();

        Set<String> loadedModules = modules.values().stream()
                // .filter(info -> info.state() == ModuleState.LOADED || info.state() == ModuleState.ENABLED)
                .map(ModuleInformation::moduleMeta)
                .map(ModuleMeta::identifier)
                .collect(Collectors.toSet());

        HashSet<String> missingDependencies = new HashSet<>();

        for (String dependency : dependencies) {
            if (!loadedModules.contains(dependency)) {
                missingDependencies.add(dependency);
            }
        }

        return missingDependencies;
    }

    private boolean hasMissingDependencies(ModuleInformation moduleInformation) {

        return !getMissingDependencies(moduleInformation).isEmpty();
    }

    private Collection<ModuleInformation> getModules(String... moduleIdentifier) {

        Set<String> identifier = new HashSet<>(Arrays.asList(moduleIdentifier));

        return this.modules.values().stream()
                .filter(moduleInformation -> identifier.contains(moduleInformation.moduleMeta().identifier()))
                .collect(Collectors.toList());
    }

    private ModuleInformation updateModuleCache(ModuleInformation moduleInformation) {
        this.modules.put(moduleInformation.moduleMeta().moduleClass(), moduleInformation);
        return moduleInformation;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(of = "moduleMeta")
    @Accessors(fluent = true)
    static class ModuleInformation {

        private final ModuleMeta moduleMeta;
        @Nullable private final Object module;
        @Nullable private final Method onLoad;
        @Nullable private final Method onEnable;
        @Nullable private final Method onDisable;
        private ModuleState state;

        @SuppressWarnings("unchecked")
        public ModuleInformation(ModuleMeta moduleMeta, @Nullable Object module) {
            this.moduleMeta = moduleMeta;
            this.module = module;
            onLoad = getAllMethods(moduleMeta.moduleClass(), withAnnotation(OnLoad.class)).stream().findFirst().orElse(null);
            onEnable = getAllMethods(moduleMeta.moduleClass(), withAnnotation(OnEnable.class)).stream().findFirst().orElse(null);
            onDisable = getAllMethods(moduleMeta.moduleClass(), withAnnotation(OnDisable.class)).stream().findFirst().orElse(null);
        }

        public Optional<Object> module() {

            return Optional.ofNullable(module);
        }

        public Configuration onLoad(Configuration configuration) {
            if (onLoad == null) return configuration;

            Object invokeMethod = invokeMethod(onLoad, configuration);
            if (invokeMethod instanceof Configuration) {
                return (Configuration) invokeMethod;
            }

            return configuration;
        }

        public void onEnable(Configuration configuration) {
            if (onEnable == null) return;
            invokeMethod(onEnable, configuration);
        }

        public void onDisable(Configuration configuration) {
            if (onDisable == null) return;
            invokeMethod(onDisable, configuration);
        }

        private Object invokeMethod(@NonNull Method method, Configuration configuration) {
            try {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i].isInstance(configuration)) {
                        parameters[i] = configuration;
                    } else {
                        parameters[i] = null;
                    }
                }
                method.setAccessible(true);
                return method.invoke(module, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
