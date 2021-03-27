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

package io.artframework.bukkit;

import io.artframework.BootstrapModule;
import io.artframework.BootstrapScope;
import io.artframework.Module;
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;
import io.artframework.bukkit.parser.CommandLineParser;
import io.artframework.bukkit.storage.EbeanPersistenceProvider;
import io.artframework.bukkit.storage.MetadataStore;
import io.artframework.bukkit.trigger.EntityDamageTrigger;
import io.artframework.bukkit.trigger.LocationTrigger;
import io.artframework.bukkit.trigger.PlayerListener;
import io.artframework.modules.scripts.ScriptsModule;
import io.artframework.util.FileUtil;
import io.ebean.Database;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/// [module]
@ArtModule(
        value = "art-bukkit",
        description = {"The base art-module for the bukkit platform, providing various actions, requirements and trigger for bukkit plugins."}
)
public class ArtBukkitModule implements BootstrapModule {
/// [module]
    private PlayerListener playerListener;
    private LocationTrigger locationTrigger;
    private EntityDamageTrigger entityDamageTrigger;
    private final ArtBukkitPlugin plugin;
    private EbeanPersistenceProvider storageProvider;

    public ArtBukkitModule(ArtBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onBootstrap(BootstrapScope scope) {

        if (Bukkit.getPluginManager().getPlugin("ebean-wrapper") != null) {
            Database database = new EbeanWrapper(Config.builder(plugin)
                    .entities(
                            MetadataStore.class
                    )
                    .build()).connect();
            storageProvider = new EbeanPersistenceProvider(scope, database);
        }

        scope.configure(builder -> {
            builder.classLoader(plugin.getClass().getClassLoader())
                    .scheduler(new BukkitScheduler(plugin, Bukkit.getScheduler()));
            if (storageProvider != null) {
                builder.storage(storageProvider);
            }
        });

        findModules(scope).forEach(scope::register);

        scope.register(new ScriptsModule());
    }

    @Override
    public void onLoad(Scope scope) {

        if (storageProvider != null) {
            storageProvider.load();
        }

        scope.configuration().parser().add(CommandLineParser::new);

        playerListener = new PlayerListener(scope);
        locationTrigger = new LocationTrigger(scope);
        entityDamageTrigger = new EntityDamageTrigger(scope);

        Bukkit.getPluginManager().registerEvents(playerListener, plugin);
        Bukkit.getPluginManager().registerEvents(locationTrigger, plugin);
        Bukkit.getPluginManager().registerEvents(entityDamageTrigger, plugin);

        scope.register()
                .trigger()
                .add(LocationTrigger.class, () -> new LocationTrigger(scope))
                .add(EntityDamageTrigger.class, () -> new EntityDamageTrigger(scope));
    }

    @Override
    public void onReload(Scope scope) {

        if (storageProvider != null) {
            storageProvider.reload();
        }
    }

    @Override
    public void onDisable(Scope scope) {

        HandlerList.unregisterAll(playerListener);
        HandlerList.unregisterAll(entityDamageTrigger);
    }

    private Collection<Class<? extends Module>> findModules(BootstrapScope scope) {

        ArrayList<Class<? extends Module>> modules = new ArrayList<>();

        File modulesDir = scope.settings().modulePath();
        modulesDir.mkdirs();

        File[] files = modulesDir.listFiles();
        if (files != null) {
            for (File moduleFile : files) {
                if (moduleFile.isFile() && moduleFile.getName().endsWith(".jar")) {
                    modules.addAll(FileUtil.findClasses(
                            scope.configuration().classLoader(),
                            moduleFile,
                            Module.class,
                            moduleClass -> moduleClass.isAnnotationPresent(ArtModule.class))
                    );
                }
            }
        }

        return modules;
    }
}
