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

import io.artframework.ART;
import io.artframework.*;
import io.artframework.annotations.*;
import io.artframework.events.*;
import io.artframework.util.ReflectionUtil;
import io.artframework.util.graphs.CycleSearch;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.*;
import java.util.stream.Collectors;

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@Log(topic = "art-framework:modules")
public class DefaultModuleProvider extends AbstractProvider implements ModuleProvider {

    final Map<Class<?>, ModuleInformation> modules = new HashMap<>();
    private CycleSearch<ModuleMeta> cycleSearcher = new CycleSearch<>(new boolean[0][0], new ModuleMeta[0]);
    private ArtModuleDependencyResolver resolver;

    public DefaultModuleProvider(@NonNull Scope scope) {
        super(scope);
    }

    public Optional<ModuleInformation> get(Class<?> moduleClass) {

        return Optional.ofNullable(modules.get(moduleClass));
    }

    @Override
    public Optional<ModuleMeta> get(@Nullable Object module) {
        if (module == null) return Optional.empty();

        return Optional.ofNullable(modules.get(module.getClass()))
                .map(ModuleInformation::moduleMeta);
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
    public ModuleProvider bootstrap(BootstrapScope bootstrapScope) throws BootstrapException {

        try {
            log.fine("Starting bootstrap process with: " + bootstrapScope.bootstrapModule().getClass().getSimpleName());
            ModuleInformation bootstrapModule = registerModule(bootstrapScope.bootstrapModule());
            bootstrapModule(bootstrapScope, bootstrapModule);

            for (Object module : bootstrapScope.bootstrapModule().modules(bootstrapScope)) {
                try {
                    if (module instanceof Class) {
                        if (((Class<?>) module).isAnnotationPresent(ArtModule.class)) {
                            registerModule((Class<?>) module);
                        } else {
                            findAndLoadAllArt(((Class<?>) module).getProtectionDomain().getCodeSource());
                        }
                    } else {
                        if (module.getClass().isAnnotationPresent(ArtModule.class)) {
                            registerModule(module);
                        } else {
                            findAndLoadAllArt(module.getClass().getProtectionDomain().getCodeSource());
                        }
                    }
                } catch (URISyntaxException | ModuleRegistrationException e) {
                    e.printStackTrace();
                }
            }

            bootstrapAll(bootstrapScope);

            loadModule(bootstrapModule);
            loadAll();

            enableModule(bootstrapModule);
            enableAll();

            log.fine("Successfully bootstrapped the art-framework with: " + bootstrapScope.bootstrapModule().getClass().getSimpleName());
        } catch (ModuleRegistrationException e) {
            disableAll();
            throw new BootstrapException(e);
        }

        return this;
    }

    public void bootstrapAll(BootstrapScope scope) {

        for (ModuleInformation module : modules.values()) {
            try {
                bootstrapModule(scope, module);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadAll() {

        for (ModuleInformation module : modules.values()) {
            try {
                loadModule(module);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }
    }

    public void enableAll() {

        for (ModuleInformation module : modules.values()) {
            try {
                enableModule(module);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }
    }

    public void disableAll() {

        for (ModuleInformation module : modules.values()) {
            try {
                disableModule(module);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }
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

        enableModule(registerModule(module));

        return this;
    }

    @Override
    public ModuleProvider enable(@NonNull Class<?> moduleClass) throws ModuleRegistrationException {

        enableModule(registerModule(moduleClass));

        return this;
    }

    @Override
    public ModuleProvider disable(@NonNull Object module) {

        ModuleInformation information = modules.remove(module.getClass());
        if (information != null) {
            try {
                disableModule(information);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public ModuleProvider reload(@NonNull Class<?> moduleClass) {

        get(moduleClass).ifPresent(this::reloadModule);

        return this;
    }

    @Override
    public ModuleProvider reloadAll() {

        modules.values().forEach(this::reloadModule);

        return this;
    }

    private ModuleInformation registerModule(Class<?> moduleClass) throws ModuleRegistrationException {

        if (modules.containsKey(moduleClass)) {
            return modules.get(moduleClass);
        }

        try {
            ModuleMeta moduleMeta = ModuleMeta.of(moduleClass);

            try {
                Object artModule = scope().configuration().injector().create(moduleClass, scope());
                return registerModule(moduleMeta, artModule);
            } catch (ReflectiveOperationException e) {
                String errorMessage = "Unable to create a new instance of the ArtModule " + moduleClass.getSimpleName() + ". " +
                        "Does it have a parameterless public constructor?";
                log.severe(errorMessage);
                throw new ModuleRegistrationException(
                        moduleMeta,
                        ModuleState.INVALID_MODULE,
                        errorMessage,
                        e
                );
            }
        } catch (ArtMetaDataException e) {
            throw new ModuleRegistrationException(null, ModuleState.INVALID_MODULE, e);
        }
    }

    private ModuleInformation registerModule(@NonNull Object module) throws ModuleRegistrationException {

        try {
            ReflectionUtil.loadConfigFields(scope(), module);
            return registerModule(ModuleMeta.of(module.getClass()), module);
        } catch (ArtMetaDataException e) {
            throw new ModuleRegistrationException(null, ModuleState.INVALID_MODULE, e);
        }
    }

    private ModuleInformation registerModule(@NonNull ModuleMeta moduleMeta, @NonNull Object module) throws ModuleRegistrationException {

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
            modules.put(moduleMeta.moduleClass(), moduleInformation);
            logState(moduleInformation);

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

    private void bootstrapModule(@NonNull BootstrapScope scope, @NonNull ModuleInformation module) throws ModuleRegistrationException {

        if (!module.state().canBootstrap()) return;

        try {
            module.onBootstrap(scope);
            updateModuleCache(module.state(ModuleState.BOOTSTRAPPED));
            ART.callEvent(new ModuleBootstrappedEvent(module.moduleMeta()));
            logState(module);
        } catch (Exception exception) {
            throw new ModuleRegistrationException(module.moduleMeta(), ModuleState.ERROR, exception);
        }
    }

    private void loadModule(ModuleInformation module) throws ModuleRegistrationException {

        if (!module.state().canLoad()) return;

        checkDependencies(module, this::loadModule);
        findAndLoadAllArt(module);

        try {
            module.onLoad(scope());
            updateModuleCache(module.state(ModuleState.LOADED));
            ART.callEvent(new ModuleLoadedEvent(module.moduleMeta()));
            logState(module);
        } catch (Exception e) {
            updateModuleCache(module.state(ModuleState.ERROR));
            logState(module, e.getMessage());
            throw new ModuleRegistrationException(module.moduleMeta(), ModuleState.ERROR,
                    "An error occured when trying to load the module \"" + module.moduleMeta().identifier() + "\": " + e.getMessage(), e);
        }
    }

    private void reloadModule(ModuleInformation module) {

        if (!module.state().canReload()) return;

        try {
            module.onReload(scope());
            ART.callEvent(new ModuleReloadedEvent(module.moduleMeta()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableModule(ModuleInformation module) throws ModuleRegistrationException {

        if (!module.state().canEnable()) return;

        loadModule(module);
        checkDependencies(module, this::enableModule);

        try {
            module.onEnable(scope());
            updateModuleCache(module.state(ModuleState.ENABLED));
            ART.callEvent(new ModuleEnabledEvent(module.moduleMeta()));
            logState(module);
        } catch (Exception e) {
            updateModuleCache(module.state(ModuleState.ERROR));
            logState(module, e.getMessage());
            throw new ModuleRegistrationException(module.moduleMeta(), ModuleState.ERROR,
                    "Encountered an error while enabling the module \"" + module.moduleMeta().identifier() + "\": " + e.getMessage(), e);
        }
    }

    private void disableModule(ModuleInformation module) throws ModuleRegistrationException {

        if (!module.state().canDisable()) return;

        try {
            module.onDisable(scope());
            updateModuleCache(module.state(ModuleState.DISABLED));
            ART.callEvent(new ModuleDisabledEvent(module.moduleMeta()));
            logState(module);
        } catch (Exception e) {
            updateModuleCache(module.state(ModuleState.ERROR));
            logState(module, e.getMessage());
            throw new ModuleRegistrationException(module.moduleMeta(), ModuleState.ERROR,
                    "Encountered an error while disabling the module \"" + module.moduleMeta().identifier() + "\": " + e.getMessage(), e);
        }
    }

    private void checkDependencies(ModuleInformation module, ChildModuleLoader childModuleAction) throws ModuleRegistrationException {
        if (hasMissingDependencies(module)) {
            updateModuleCache(module.state(ModuleState.MISSING_DEPENDENCIES));
            String missingDeps = " missing the following dependencies: " + String.join(",", getMissingDependencies(module));
            logState(module, missingDeps);
            throw new ModuleRegistrationException(module.moduleMeta(), module.state(),
                    "The module \"" + module.moduleMeta().identifier() + "\" is" + missingDeps);
        }

        for (ModuleInformation childModule : getModules(module.moduleMeta().dependencies())) {
            try {
                childModuleAction.accept(childModule);
            } catch (ModuleRegistrationException e) {
                updateModuleCache(module.state(ModuleState.DEPENDENCY_ERROR));
                logState(module, e.getMessage());
                throw new ModuleRegistrationException(module.moduleMeta(), ModuleState.DEPENDENCY_ERROR,
                        "Failed to enable the module \"" + module.moduleMeta().identifier() + "\" because a child module could not be enabled: " + e.getMessage(), e);
            }
        }
    }

    private void findAndLoadAllArt(ModuleInformation module) throws ModuleRegistrationException {
        try {
            CodeSource codeSource = module.moduleMeta().moduleClass().getProtectionDomain().getCodeSource();
            findAndLoadAllArt(codeSource);
        } catch (URISyntaxException e) {
            updateModuleCache(module.state(ModuleState.ERROR));
            logState(module, e.getMessage());
            throw new ModuleRegistrationException(module.moduleMeta(), ModuleState.ERROR,
                    "An error occured when trying to load the art in module \"" + module.moduleMeta().identifier() + "\": " + e.getMessage(), e);
        }
    }

    private void findAndLoadAllArt(CodeSource codeSource) throws URISyntaxException {
        if (codeSource != null) {
            configuration().finder().findAllAndLoadIn(new File(codeSource.getLocation().toURI()),
                    aClass -> !BootstrapModule.class.isAssignableFrom(aClass) && !aClass.isAnnotationPresent(ArtModule.class)
            );
        }
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

    private static void logState(ModuleInformation module, String... messages) {
        String msg = "[" + module.state().name() + "] " + module.moduleMeta().identifier() + " - " + module.moduleMeta().moduleClass().getCanonicalName()
                + (messages.length > 0 ? ": " + String.join(";", messages) : "");
        if (module.state().error()) {
            log.severe(msg);
        } else {
            log.info(msg);
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode(of = "moduleMeta")
    @Accessors(fluent = true)
    static class ModuleInformation {

        private final ModuleMeta moduleMeta;
        @Nullable private final Object module;
        @Nullable private final Method onBootstrap;
        @Nullable private final Method onLoad;
        @Nullable private final Method onEnable;
        @Nullable private final Method onDisable;
        @Nullable private final Method onReload;
        private ModuleState state;

        @SuppressWarnings("unchecked")
        public ModuleInformation(ModuleMeta moduleMeta, @Nullable Object module) {
            this.moduleMeta = moduleMeta;
            this.module = module;
            Class<?> moduleClass = moduleMeta.moduleClass();
            if (BootstrapModule.class.isAssignableFrom(moduleClass)) {
                try {
                    onBootstrap = moduleClass.getMethod("onBootstrap", BootstrapScope.class);
                    onLoad = moduleClass.getMethod("onLoad", Scope.class);
                    onEnable = moduleClass.getMethod("onEnable", Scope.class);
                    onDisable = moduleClass.getMethod("onDisable", Scope.class);
                    onReload = moduleClass.getMethod("onReload", Scope.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            } else {
                onBootstrap = getAllMethods(moduleClass, withAnnotation(OnBootstrap.class)).stream().findFirst().orElse(null);
                onLoad = getAllMethods(moduleClass, withAnnotation(OnLoad.class)).stream().findFirst().orElse(null);
                onEnable = getAllMethods(moduleClass, withAnnotation(OnEnable.class)).stream().findFirst().orElse(null);
                onDisable = getAllMethods(moduleClass, withAnnotation(OnDisable.class)).stream().findFirst().orElse(null);
                onReload = getAllMethods(moduleClass, withAnnotation(OnReload.class)).stream().findFirst().orElse(null);
            }
        }

        public Optional<Object> module() {

            return Optional.ofNullable(module);
        }

        public void onBootstrap(BootstrapScope scope) {
            if (onBootstrap == null) return;
            invokeMethod(onBootstrap, scope);
        }

        public void onLoad(Scope scope) {
            if (onLoad == null) return;
            invokeMethod(onLoad, scope);
        }

        public void onEnable(Scope scope) {
            if (onEnable == null) return;
            invokeMethod(onEnable, scope);
        }

        public void onDisable(Scope scope) {
            if (onDisable == null) return;
            invokeMethod(onDisable, scope);
        }

        public void onReload(Scope scope) {
            if (onReload == null) return;
            invokeMethod(onReload, scope);
        }

        private void invokeMethod(@NonNull Method method, Scope scope) {
            try {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i].isInstance(scope)) {
                        parameters[i] = scope;
                    } else {
                        parameters[i] = null;
                    }
                }
                method.setAccessible(true);
                method.invoke(module, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @FunctionalInterface
    public interface ChildModuleLoader {

        void accept(ModuleInformation moduleInformation) throws ModuleRegistrationException;
    }
}
