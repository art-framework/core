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
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;
import io.artframework.bukkit.actions.CancelBukkitEventAction;
import io.artframework.bukkit.actions.DamageLivingEntityAction;
import io.artframework.bukkit.actions.SendMessageAction;
import io.artframework.bukkit.requirements.HealthRequirement;
import io.artframework.bukkit.trigger.EntityTrigger;
import io.artframework.bukkit.trigger.PlayerServerTrigger;
import io.artframework.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

@ArtModule("art-bukkit")
public class ArtBukkitModule implements BootstrapModule {

    private final PlayerServerTrigger playerServerTrigger = new PlayerServerTrigger();
    private final EntityTrigger entityTrigger = new EntityTrigger();
    private final ArtBukkitPlugin plugin;

    public ArtBukkitModule(ArtBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Collection<Object> modules(BootstrapScope scope) {

        ArrayList<Object> modules = new ArrayList<>();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().isAnnotationPresent(ArtModule.class)) {
                modules.add(plugin);
            }
        }

        File modulesDir = scope.settings().modulePath();
        modulesDir.mkdirs();

        File[] files = modulesDir.listFiles();
        if (files != null) {
            for (File moduleFile : files) {
                if (moduleFile.isFile() && moduleFile.getName().endsWith(".jar")) {
                    modules.addAll(FileUtil.findClasses(
                            scope.configuration().classLoader(),
                            moduleFile,
                            moduleClass -> moduleClass.isAnnotationPresent(ArtModule.class))
                    );
                }
            }
        }

        return modules;
    }

    @Override
    public void onBootstrap(BootstrapScope scope) {

        scope.configure(builder -> builder
                .classLoader(plugin.getClass().getClassLoader())
                .scheduler(new BukkitScheduler(plugin, Bukkit.getScheduler()))
        );
    }

    @Override
    public void onLoad(Scope scope) {

        Bukkit.getPluginManager().registerEvents(playerServerTrigger, plugin);
        Bukkit.getPluginManager().registerEvents(entityTrigger, plugin);

        scope.configuration()
                .actions()
                    .add(CancelBukkitEventAction.class)
                    .add(DamageLivingEntityAction.class)
                    .add(SendMessageAction.class)
                .requirements()
                    .add(HealthRequirement.class)
                .trigger()
                    .add(playerServerTrigger)
                    .add(entityTrigger);
    }

    @Override
    public void onDisable(Scope scope) {

        HandlerList.unregisterAll(playerServerTrigger);
        HandlerList.unregisterAll(entityTrigger);
    }
}
