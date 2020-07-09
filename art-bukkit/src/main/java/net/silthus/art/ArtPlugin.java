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

package net.silthus.art;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.scheduler.Scheduler;
import net.silthus.art.parser.flow.FlowParserModule;
import net.silthus.art.scheduler.BukkitScheduler;
import net.silthus.art.storage.persistence.PersistenceModule;
import net.silthus.art.targets.EntityTarget;
import net.silthus.art.targets.LivingEntityTarget;
import net.silthus.art.targets.OfflinePlayerTarget;
import net.silthus.art.targets.PlayerTarget;
import net.silthus.slib.bukkit.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import javax.inject.Inject;

@PluginMain
public class ArtPlugin extends BasePlugin {

    @Inject
    @Getter
    private ArtManager artManager;

    @Override
    public void enable() {

        ART.setInstance(artManager);
        ART.load();

        ART.register(new ArtBukkitDescription(this), artBuilder -> artBuilder
                .target(Entity.class)
                    .filter(new EntityWorldFilter())
                    .and()
                    .target(EntityTarget::new)
                .and(Player.class)
                    .target(PlayerTarget::new)
                .and(LivingEntity.class)
                    .target(LivingEntityTarget::new)
                .and(OfflinePlayer.class)
                    .target(OfflinePlayerTarget::new)
        );

    Bukkit.getServicesManager().register(ArtManager.class, artManager, this, ServicePriority.Normal);
    }

    @Override
    public void disable() {

        ART.getInstance().ifPresent(ArtManager::unload);

        Bukkit.getServicesManager().unregisterAll(this);
    }

    @Override
    public void configure(Binder binder) {

        binder.install(new BukkitModule(this));
        binder.install(new ArtGuiceModule());
        binder.install(new FlowParserModule());
        binder.install(new PersistenceModule());
        binder.bind(Scheduler.class).to(BukkitScheduler.class);
    }

    @Provides
    @Named("SPIGOT_CLASSLOADER")
    public ClassLoader provideClassLoader() {
        return getClassLoader();
    }
}
