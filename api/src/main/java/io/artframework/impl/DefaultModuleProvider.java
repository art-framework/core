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

import io.artframework.Module;
import io.artframework.*;
import io.artframework.util.ConfigUtil;
import io.artframework.util.ReflectionUtil;
import io.artframework.util.graphs.CycleSearch;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.CodeSource;
import java.util.*;
import java.util.stream.Collectors;

@Log(topic = "art-framework")
public class DefaultModuleProvider extends AbstractProvider implements ModuleProvider {

    final Map<Class<? extends Module>, ModuleInformation> modules = new HashMap<>();
    private CycleSearch<ModuleMeta> cycleSearcher = new CycleSearch<>(new boolean[0][0], new ModuleMeta[0]);
    private ArtModuleDependencyResolver resolver;

    private BootstrapScope bootstrapScope;
    private Lifecycle lifecycle = Lifecycle.PRE_BOOTSTRAP;

    public DefaultModuleProvider(@NonNull Scope scope) {
        super(scope);
    }

    protected Optional<ModuleInformation> getModuleInformation(Class<? extends Module> moduleClass) {

        return Optional.ofNullable(modules.get(moduleClass));
    }

    @Override
    public Collection<ModuleMeta> all() {
        return modules.values().stream()
                .map(ModuleInformation::moduleMeta)
                .collect(Collectors.toList());
    }

    @Override
    public <TModule extends Module> Optional<TModule> get(@NonNull Class<TModule> moduleClass) {

        return getModuleInformation(moduleClass)
                .flatMap(ModuleInformation::module)
                .map(moduleClass::cast);
    }

    @Override
    public Optional<ModuleMeta> getMetadata(@NonNull Class<? extends Module> moduleClass) {

        return getModuleInformation(moduleClass)
                .map(ModuleInformation::moduleMeta);
    }

    @Override
    public Optional<ModuleMeta> getSourceModule(@NonNull Class<?> clazz) {

        CodeSource source = clazz.getProtectionDomain().getCodeSource();
        if (source == null) return Optional.empty();

        return modules.values().stream()
                .map(ModuleInformation::moduleMeta)
                .filter(moduleMeta -> source.equals(moduleMeta.moduleClass().getProtectionDomain().getCodeSource()))
                .findFirst();
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

        if (lifecycle != Lifecycle.PRE_BOOTSTRAP) {
            throw new BootstrapException("The art-framework is already bootstrapped and cannot initialize again!");
        }

        try {
            log.fine("Starting bootstrap process with: " + bootstrapScope.bootstrapModule().getClass().getCanonicalName());
            ModuleInformation bootstrapModule = registerModule(bootstrapScope.bootstrapModule());
            bootstrapModule(bootstrapScope, bootstrapModule);

            lifecycle = Lifecycle.BOOTSTRAPPED_ROOT_MODULE;
            this.bootstrapScope = bootstrapScope;

            bootstrapAll(bootstrapScope);

            log.fine("Successfully bootstrapped the art-framework with: " + bootstrapScope.bootstrapModule().getClass().getCanonicalName());
        } catch (ModuleRegistrationException e) {
            disableAll();
            throw new BootstrapException(e);
        }

        return this;
    }

    public void bootstrapAll(BootstrapScope scope) {

        if (lifecycle.cannotBootstrap()) {
            throw new BootstrapException("The art-framework modules have already been bootstrapped and cannot be bootstrapped again!");
        }

        for (ModuleInformation module : modules.values()) {
            try {
                if (!module.moduleMeta().bootstrapModule())
                    bootstrapModule(scope, module);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }

        lifecycle = Lifecycle.BOOTSTRAPPED;
    }

    @Override
    public void loadAll() {

        if (lifecycle.loaded()) {
            throw new BootstrapException("The art-framework modules have already been loaded and cannot be loaded again!");
        }

        for (ModuleInformation module : modules.values()) {
            try {
                loadModule(module);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }

        lifecycle = Lifecycle.LOADED;
    }

    @Override
    public void enableAll() {

        if (lifecycle.enabled()) {
            throw new BootstrapException("The art-framework modules have already been enabled and cannot be enabled again!");
        }

        for (ModuleInformation module : modules.values()) {
            try {
                enableModule(module);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }

        lifecycle = Lifecycle.ENABLED;
    }

    @Override
    public void disableAll() {

        if (lifecycle.disabled()) {
            throw new BootstrapException("The art-framework modules have already been disabled and cannot be disabled again!");
        }

        for (ModuleInformation module : modules.values()) {
            try {
                disableModule(module);
            } catch (ModuleRegistrationException e) {
                e.printStackTrace();
            }
        }

        lifecycle = Lifecycle.DISABLED;
    }

    @Override
    public ModuleProvider register(@NonNull Module module) throws ModuleRegistrationException {

        registerModule(module);

        return this;
    }

    @Override
    public ModuleProvider register(@NonNull Class<? extends Module> moduleClass) throws ModuleRegistrationException {

        registerModule(moduleClass);

        return this;
    }

    @Override
    public ModuleProvider enable(@NonNull Class<? extends Module> moduleClass) throws ModuleRegistrationException {

        getModuleInformation(moduleClass).ifPresent(this::enableModule);

        return this;
    }

    @Override
    public ModuleProvider disable(@NonNull Class<? extends Module> moduleClass) {

        getModuleInformation(moduleClass).ifPresent(this::disableModule);

        return this;
    }

    @Override
    public ModuleProvider reload(@NonNull Class<? extends Module> moduleClass) {

        getModuleInformation(moduleClass).ifPresent(this::reloadModule);

        return this;
    }

    @Override
    public ModuleProvider reloadAll() {

        modules.values().forEach(this::reloadModule);

        return this;
    }

    private void registerModule(Class<? extends Module> moduleClass) throws ModuleRegistrationException {

        if (modules.containsKey(moduleClass)) {
            modules.get(moduleClass);
            return;
        }

        try {
            ModuleMeta moduleMeta = ModuleMeta.of(moduleClass);

            try {
                Module artModule = scope().configuration().injector().create(moduleClass, scope());
                registerModule(moduleMeta, artModule);
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

    private ModuleInformation registerModule(@NonNull Module module) throws ModuleRegistrationException {

        try {
            ConfigUtil.injectConfigFields(scope(), module);
            return registerModule(ModuleMeta.of(module.getClass()), module);
        } catch (ArtMetaDataException e) {
            throw new ModuleRegistrationException(null, ModuleState.INVALID_MODULE, e);
        }
    }

    private ModuleInformation registerModule(@NonNull ModuleMeta moduleMeta, @NonNull Module module) throws ModuleRegistrationException {

        Optional<ModuleMeta> existingModule = modules.values().stream().map(ModuleInformation::moduleMeta)
                .filter(meta -> meta.identifier().equals(moduleMeta.identifier())
                        && !meta.moduleClass().equals(moduleMeta.moduleClass())).findAny();
        if (existingModule.isPresent()) {
            throw new ModuleRegistrationException(moduleMeta, ModuleState.DUPLICATE_MODULE,
                    "There is already a module named \"" + moduleMeta.identifier() + "\" registered: "
                            + existingModule.get().moduleClass().getCanonicalName());
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
                        "The module \"" + moduleInformation.moduleMeta().identifier()
                                + "\" has cyclic dependencies: " + dependencyGraphToString(dependencyGraph.get()));
            }
        }

        if (lifecycle.enabled()) {
            enableModule(moduleInformation);
        } else if (lifecycle.loaded()) {
            loadModule(moduleInformation);
        } else if (lifecycle.bootstrapped()) {
            bootstrapModule(bootstrapScope, moduleInformation);
        }

        return moduleInformation;
    }

    private void bootstrapModule(@NonNull BootstrapScope scope, @NonNull ModuleInformation module) throws ModuleRegistrationException {

        if (!module.state().canBootstrap()) return;

        try {
            module.onBootstrap(scope);
            updateModuleCache(module.state(ModuleState.BOOTSTRAPPED));
            logState(module);
        } catch (Exception exception) {
            throw new ModuleRegistrationException(module.moduleMeta(), ModuleState.ERROR, exception);
        }
    }

    private void loadModule(ModuleInformation module) throws ModuleRegistrationException {

        if (!module.state().canLoad()) return;

        checkDependencies(module, this::loadModule);
        if (scope().settings().autoRegisterAllArt()) {
            findAndLoadAllArt(module);
        }

        try {
            module.onLoad(scope());
            updateModuleCache(module.state(ModuleState.LOADED));
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
            module.module().ifPresent(o -> ConfigUtil.injectConfigFields(scope(), o));
            module.onReload(scope());
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void findAndLoadAllArt(ModuleInformation module) throws ModuleRegistrationException {

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(module.moduleMeta.moduleClass().getProtectionDomain().getCodeSource().getLocation())
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage(module.moduleMeta.packages()))
        );

        // actions
        for (Class<? extends Action> aClass : reflections.getSubTypesOf(Action.class)) {
            if (!GenericAction.class.equals(aClass)) {
                scope().register().actions().add((Class<? extends Action<?>>) aClass);
            }
        }
        // requirements
        for (Class<? extends Requirement> aClass : reflections.getSubTypesOf(Requirement.class)) {
            if (!GenericRequirement.class.equals(aClass)) {
                scope().register().requirements().add((Class<? extends Requirement<?>>) aClass);
            }
        }
        // trigger
        for (Class<? extends Trigger> aClass : reflections.getSubTypesOf(Trigger.class)) {
            scope().register().trigger().add(aClass);
        }
        // targets
        for (Class<? extends Target> aClass : reflections.getSubTypesOf(Target.class)) {
            Optional<Class<?>> sourceClass = ReflectionUtil.getInterfaceTypeArgument(aClass, Target.class, 0);
            sourceClass.ifPresent(targetClass -> {
                try {
                    Constructor<? extends Target> constructor = aClass.getDeclaredConstructor(targetClass);
                    scope().register().targets().add(targetClass, target -> {
                        try {
                            constructor.setAccessible(true);
                            return constructor.newInstance(target);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            return null;
                        }
                    });
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            });
        }
        // replacements
        for (Class<? extends Replacement> aClass : reflections.getSubTypesOf(Replacement.class)) {
            try {
                configuration().replacements().add(aClass.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.warning("failed to create instance of replacement: " + aClass.getCanonicalName());
            }
        }
        // resolver
        for (Class<? extends Resolver> aClass : reflections.getSubTypesOf(Resolver.class)) {
            scope().register().resolvers().add(aClass);
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

            if (i == graph.size() - 1) {
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
            log.finest(msg);
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode(of = "moduleMeta")
    @Accessors(fluent = true)
    static class ModuleInformation {

        private final ModuleMeta moduleMeta;
        @Nullable
        private final Module module;
        private ModuleState state;

        public ModuleInformation(ModuleMeta moduleMeta, @Nullable Module module) {
            this.moduleMeta = moduleMeta;
            this.module = module;
        }

        public Optional<Module> module() {

            return Optional.ofNullable(module);
        }

        void onBootstrap(BootstrapScope scope) throws Exception {

            if (module().isPresent()) {
                module().get().onBootstrap(scope);
            }
        }

        void onLoad(Scope scope) throws Exception {

            if (module().isPresent()) {
                module().get().onLoad(scope);
            }
        }

        void onEnable(Scope scope) throws Exception {

            if (module().isPresent()) {
                module().get().onEnable(scope);
            }
        }

        void onDisable(Scope scope) throws Exception {

            if (module().isPresent()) {
                module().get().onDisable(scope);
            }
        }

        void onReload(Scope scope) throws Exception {

            if (module().isPresent()) {
                module().get().onReload(scope);
            }
        }
    }

    @FunctionalInterface
    public interface ChildModuleLoader {

        void accept(ModuleInformation moduleInformation) throws ModuleRegistrationException;
    }

    enum Lifecycle {
        PRE_BOOTSTRAP,
        BOOTSTRAPPED_ROOT_MODULE,
        BOOTSTRAPPED,
        LOADED,
        ENABLED,
        DISABLED;

        boolean cannotBootstrap() {

            return this != BOOTSTRAPPED_ROOT_MODULE;
        }

        public boolean bootstrapped() {

            return this == BOOTSTRAPPED;
        }

        boolean loaded() {

            switch (this) {
                case LOADED:
                case ENABLED:
                case DISABLED:
                    return true;
                default:
                    return false;
            }
        }

        boolean enabled() {

            return this == Lifecycle.ENABLED;
        }

        boolean disabled() {

            return this == Lifecycle.DISABLED;
        }
    }
}
