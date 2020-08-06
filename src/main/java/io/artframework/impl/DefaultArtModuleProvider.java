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
import io.artframework.annotations.ART;
import io.artframework.annotations.Depends;
import io.artframework.events.ModuleDisabledEvent;
import io.artframework.events.ModuleEnabledEvent;
import io.artframework.events.ModuleRegisteredEvent;
import io.artframework.util.graphs.CycleSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultArtModuleProvider extends AbstractProvider implements ArtModuleProvider {

    final Map<ModuleMeta, ModuleInformation> modules = new HashMap<>();
    private CycleSearch<ModuleMeta> cycleSearcher = new CycleSearch<>(new boolean[0][0], new ModuleMeta[0]);
    private ArtModuleDependencyResolver resolver;

    public DefaultArtModuleProvider(@NonNull Configuration configuration) {
        super(configuration);
    }

    @Override
    public ArtModuleProvider resolver(@Nullable ArtModuleDependencyResolver resolver) {
        this.resolver = resolver;
        return this;
    }

    @Override
    public Optional<ArtModuleDependencyResolver> resolver() {
        return Optional.ofNullable(resolver);
    }

    @Override
    public ArtModuleProvider register(@NonNull ArtModule module) throws ModuleRegistrationException {

        registerModule(module);

        return this;
    }

    @Override
    public ArtModuleProvider load(@NonNull ArtModule module) throws ModuleRegistrationException {

        enableModule(registerModule(module));

        return this;
    }

    @Override
    public ArtModuleProvider unload(@NonNull ArtModule module) {

        getModuleMeta(module).ifPresent(moduleMeta -> {
            ModuleInformation moduleInformation = modules.remove(moduleMeta);
            if (moduleInformation != null) {
                disableModule(moduleInformation);
            }
        });

        return this;
    }

    private ModuleInformation registerModule(ArtModule module) throws ModuleRegistrationException {
        Optional<ModuleMeta> meta = getModuleMeta(module);
        if (meta.isPresent()) {
            return registerModule(meta.get(), module);
        }

        throw new ModuleRegistrationException(null, ModuleState.INVALID_MODULE,
                "The module class " + module.getClass().getSimpleName() + " is missing the required @ART annotation.");
    }

    private ModuleInformation registerModule(ModuleMeta moduleMeta, ArtModule module) throws ModuleRegistrationException {
        Optional<ModuleMeta> existingModule = modules.keySet().stream()
                .filter(meta -> meta.identifier().equals(moduleMeta.identifier()) && !meta.moduleClass().equals(moduleMeta.moduleClass())).findAny();
        if (existingModule.isPresent()) {
            throw new ModuleRegistrationException(moduleMeta, ModuleState.DUPLICATE_MODULE,
                    "There is already a module named \"" + moduleMeta.identifier() + "\" registered: " + existingModule.get().moduleClass().getCanonicalName());
        }

        ModuleInformation moduleInformation;
        if (modules.containsKey(moduleMeta)) {
            moduleInformation = this.modules.get(moduleMeta);
        } else {
            moduleInformation = updateModuleCache(new ModuleInformation(moduleMeta, module).state(ModuleState.REGISTERED));
            io.artframework.ART.callEvent(new ModuleRegisteredEvent(moduleMeta, module));
            cycleSearcher = CycleSearch.of(modules.keySet());

            Optional<List<ModuleMeta>> dependencyGraph = getDependencyGraph(moduleInformation);
            if (dependencyGraph.isPresent()) {
                updateModuleCache(moduleInformation.state(ModuleState.CYCLIC_DEPENDENCIES));
                throw new ModuleRegistrationException(moduleInformation.moduleMeta(), moduleInformation.state(),
                        "The module \"" + moduleInformation.moduleMeta().identifier() + "\" has cyclic dependencies: " + dependencyGraphToString(dependencyGraph.get()));
            }
        }

        return moduleInformation;
    }

    private void enableModule(ModuleInformation moduleInformation) throws ModuleRegistrationException {

        if (moduleInformation.state() == ModuleState.ENABLED) return;

        if (hasMissingDependencies(moduleInformation)) {
            updateModuleCache(moduleInformation.state(ModuleState.MISSING_DEPENDENCIES));
            throw new ModuleRegistrationException(moduleInformation.moduleMeta(), moduleInformation.state(),
                    "The module \"" + moduleInformation.moduleMeta().identifier() + "\" is missing the following dependencies: " + String.join(",", getMissingDependencies(moduleInformation)));
        }

        for (ModuleInformation childModule : getModules(moduleInformation.moduleMeta().dependencies())) {
            try {
                enableModule(childModule);
            } catch (ModuleRegistrationException e) {
                updateModuleCache(moduleInformation.state(ModuleState.DEPENDENCY_ERROR));
                throw new ModuleRegistrationException(moduleInformation.moduleMeta(), ModuleState.DEPENDENCY_ERROR,
                        "Failed to enable the module \"" + moduleInformation.moduleMeta().identifier() + "\" because a child module could not be enabled: " + e.getMessage(), e);
            }
        }

        try {
            moduleInformation.module().onEnable(configuration());
            updateModuleCache(moduleInformation.state(ModuleState.ENABLED));
            io.artframework.ART.callEvent(new ModuleEnabledEvent(moduleInformation.moduleMeta(), moduleInformation.module()));
        } catch (Exception e) {
            updateModuleCache(moduleInformation.state(ModuleState.ERROR));
            throw new ModuleRegistrationException(moduleInformation.moduleMeta(), ModuleState.ERROR,
                    "An error occured when trying to enable the module \"" + moduleInformation.moduleMeta().identifier() + "\": " + e.getMessage(), e);
        }
    }

    private void disableModule(ModuleInformation module) {

        io.artframework.ART.callEvent(new ModuleDisabledEvent(module.moduleMeta(), module.module()));
    }

    private Optional<ModuleMeta> getModuleMeta(@NonNull ArtModule module) {

        if (!module.getClass().isAnnotationPresent(ART.class)) {
            return Optional.empty();
        }

        return Optional.of(ModuleMeta.of(module.getClass(), module.getClass().getAnnotation(ART.class), module.getClass().getAnnotation(Depends.class)));
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
        this.modules.put(moduleInformation.moduleMeta(), moduleInformation);
        return moduleInformation;
    }

    @Data
    @EqualsAndHashCode(of = "moduleMeta")
    @Accessors(fluent = true)
    static class ModuleInformation {

        private final ModuleMeta moduleMeta;
        private final ArtModule module;
        private ModuleState state;
    }
}
