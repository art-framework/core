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
import io.artframework.annotations.OnReload;
import io.artframework.bukkit.actions.CancelBukkitEventAction;
import io.artframework.bukkit.actions.DamageLivingEntityAction;
import io.artframework.bukkit.actions.SendMessageAction;
import io.artframework.bukkit.requirements.HealthRequirement;
import io.artframework.bukkit.requirements.LocationRequirement;
import io.artframework.bukkit.storage.EbeanPersistenceProvider;
import io.artframework.bukkit.storage.MetadataStore;
import io.artframework.bukkit.targets.*;
import io.artframework.bukkit.trigger.EntityTrigger;
import io.artframework.bukkit.trigger.PlayerServerTrigger;
import io.artframework.bukkit.trigger.LocationTrigger;
import io.artframework.modules.scripts.ScriptsModule;
import io.artframework.util.FileUtil;
import io.ebean.Database;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

@ArtModule("art-bukkit")
public class ArtBukkitModule implements BootstrapModule {

    private final PlayerServerTrigger playerServerTrigger = new PlayerServerTrigger();
    private final EntityTrigger entityTrigger = new EntityTrigger();
    private final LocationTrigger locationTrigger = new LocationTrigger();
    private final ArtBukkitPlugin plugin;
    private EbeanPersistenceProvider storageProvider;

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

        // add static modules that are included by default
        modules.add(new ScriptsModule());

        return modules;
    }

    @Override
    public void onBootstrap(BootstrapScope scope) {

        Database database = new EbeanWrapper(Config.builder(plugin)
                .entities(
                    MetadataStore.class
                )
                .build()).connect();

        storageProvider = new EbeanPersistenceProvider(scope, database);

        scope.configure(builder -> builder
                .classLoader(plugin.getClass().getClassLoader())
                .scheduler(new BukkitScheduler(plugin, Bukkit.getScheduler()))
                .storage(storageProvider)
        );
    }

    @Override
    public void onLoad(Scope scope) {

        storageProvider.load();

        Bukkit.getPluginManager().registerEvents(playerServerTrigger, plugin);
        Bukkit.getPluginManager().registerEvents(entityTrigger, plugin);
        Bukkit.getPluginManager().registerEvents(locationTrigger, plugin);

        scope.register()
                .actions()
                    .add(CancelBukkitEventAction.class)
                    .add(DamageLivingEntityAction.class)
                    .add(SendMessageAction.class)
                .requirements()
                    .add(HealthRequirement.class)
                    .add(LocationRequirement.class)
                .trigger()
                    .add(playerServerTrigger)
                    .add(entityTrigger)
                    .add(locationTrigger)
                .targets()
                    .add(Block.class, BlockTarget::new)
                    .add(Cancellable.class, CancellableEventTarget::new)
                    .add(CommandSender.class, CommandSenderTarget::new)
                    .add(Entity.class, EntityTarget::new)
                    .add(LivingEntity.class, LivingEntityTarget::new)
                    .add(Location.class, LocationTarget::new)
                    .add(Player.class, PlayerTarget::new)
                    .add(OfflinePlayer.class, OfflinePlayerTarget::new);
    }

    @OnReload
    public void onReload() {

        storageProvider.reload();
    }

    @Override
    public void onDisable(Scope scope) {

        HandlerList.unregisterAll(playerServerTrigger);
        HandlerList.unregisterAll(entityTrigger);
    }
}
