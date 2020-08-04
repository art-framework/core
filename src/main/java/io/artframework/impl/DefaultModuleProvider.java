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
import io.artframework.Configuration;
import io.artframework.Module;
import io.artframework.ModuleMeta;
import io.artframework.ModuleProvider;
import io.artframework.ModuleRegistrationException;
import io.artframework.ModuleState;
import io.artframework.annotations.ART;
import io.artframework.annotations.Depends;
import io.artframework.events.ModuleDisabledEvent;
import io.artframework.events.ModuleEnabledEvent;
import io.artframework.events.ModuleLoadedEvent;
import io.artframework.util.graphs.CycleSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultModuleProvider extends AbstractProvider implements ModuleProvider {

    private final Map<ModuleMeta, ModuleInformation> modules = new HashMap<>();
    private CycleSearch<ModuleMeta> cycleSearcher = new CycleSearch<>(new boolean[0][0], new ModuleMeta[0]);

    public DefaultModuleProvider(@NonNull Configuration configuration) {
        super(configuration);
    }

    @Override
    public ModuleProvider load(@NonNull Module module) throws ModuleRegistrationException {

        Optional<ModuleMeta> optionalModuleMeta = getModuleMeta(module);

        if (optionalModuleMeta.isPresent()) {
            ModuleMeta moduleMeta = optionalModuleMeta.get();
            ModuleInformation moduleInformation = new ModuleInformation(moduleMeta, module).state(ModuleState.LOADED);
            modules.put(moduleMeta, moduleInformation);
            cycleSearcher = CycleSearch.of(modules.keySet());

            io.artframework.ART.callEvent(new ModuleLoadedEvent(moduleMeta, module));

            enableModule(moduleInformation);
        } else {
            throw new ModuleRegistrationException(null, ModuleState.ERROR,
                    "The module class " + module.getClass().getSimpleName() + " is missing the required @ART annotation.");
        }

        return this;
    }

    @Override
    public ModuleProvider unload(@NonNull Module module) {

        getModuleMeta(module).ifPresent(moduleMeta -> {
            ModuleInformation moduleInformation = modules.remove(moduleMeta);
            if (moduleInformation != null) {
                disableModule(moduleInformation);
            }
        });

        return this;
    }

    private void enableModule(ModuleInformation moduleInformation) throws ModuleRegistrationException {

        if (moduleInformation.state() == ModuleState.ENABLED) return;

        if (hasMissingDependencies(moduleInformation)) {
            moduleInformation.state(ModuleState.MISSING_DEPENDENCIES);
            throw new ModuleRegistrationException(moduleInformation.moduleMeta(), moduleInformation.state(),
                    "The module \"" + moduleInformation.moduleMeta().identifier() + "\" is missing the following dependencies: " + String.join(",", getMissingDependencies(moduleInformation)));
        }

        Optional<List<ModuleMeta>> dependencyGraph = getDependencyGraph(moduleInformation);
        if (dependencyGraph.isPresent()) {
            moduleInformation.state(ModuleState.CYCLIC_DEPENDENCIES);
            throw new ModuleRegistrationException(moduleInformation.moduleMeta(), moduleInformation.state(),
                    "The module \"" + moduleInformation.moduleMeta().identifier() + "\" has cyclic dependencies: " + dependencyGraphToString(dependencyGraph.get()));
        }

        for (ModuleInformation childModule : getModules(moduleInformation.moduleMeta().dependencies())) {
            try {
                enableModule(childModule);
            } catch (ModuleRegistrationException e) {
                moduleInformation.state(ModuleState.DEPENDENCY_ERROR);
                throw new ModuleRegistrationException(moduleInformation.moduleMeta(), ModuleState.DEPENDENCY_ERROR,
                        "Failed to enable the module \"" + moduleInformation.moduleMeta().identifier() + "\" because a child module could not be enabled: " + e.getMessage(), e);
            }
        }

        try {
            moduleInformation.module().onEnable(configuration());
            io.artframework.ART.callEvent(new ModuleEnabledEvent(moduleInformation.moduleMeta(), moduleInformation.module()));
        } catch (Exception e) {
            moduleInformation.state(ModuleState.ERROR);
            throw new ModuleRegistrationException(moduleInformation.moduleMeta(), ModuleState.ERROR,
                    "An error occured when trying to enable the module \"" + moduleInformation.moduleMeta().identifier() + "\": " + e.getMessage(), e);
        }
    }

    private void disableModule(ModuleInformation module) {

        io.artframework.ART.callEvent(new ModuleDisabledEvent(module.moduleMeta(), module.module()));
    }

    private Optional<ModuleMeta> getModuleMeta(@NonNull Module module) {

        if (!module.getClass().isAnnotationPresent(ART.class)) {
            return Optional.empty();
        }

        return Optional.of(ModuleMeta.of(module.getClass().getAnnotation(ART.class), module.getClass().getAnnotation(Depends.class)));
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
            sb.append(graph.get(i)).append(" --> ");

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

    @Data
    @EqualsAndHashCode(of = "moduleMeta")
    @Accessors(fluent = true)
    static class ModuleInformation {

        private final ModuleMeta moduleMeta;
        private final Module module;
        private ModuleState state;
    }
}
