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

package net.silthus.examples.art;

import de.exlll.configlib.configs.yaml.YamlConfiguration;
import kr.entree.spigradle.Plugin;
import lombok.Getter;
import lombok.Setter;
import net.silthus.art.ART;
import net.silthus.art.ArtBukkitDescription;
import net.silthus.art.api.ArtManager;
import net.silthus.art.api.config.ArtConfig;
import net.silthus.art.api.parser.ArtResult;
import net.silthus.examples.art.actions.PlayerDamageAction;
import net.silthus.examples.art.listener.PlayerListener;
import net.silthus.examples.art.requirements.EntityLocationRequirement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Optional;

@Plugin
public class ExampleArtPlugin extends JavaPlugin {

    @Getter
    @Setter
    private ArtResult artResult;

    @Override
    public void onEnable() {

        // register your actions, requirements and trigger when enabling your plugin
        // this needs to be done before loading configs
        registerART();

        // this will load all art configs after all plugins are loaded and enabled
        // this is a must to avoid loading conflicts
        Bukkit.getScheduler().runTaskLater(this, this::loadARTConfig, 1L);

        // register your standard event stuff
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void registerART() {

        if (getARTManager().isEmpty()) {
            getLogger().warning("ART plugin not found. Not registering ART.");
            return;
        }

        getARTManager().get().register(ArtBukkitDescription.ofPlugin(this), artBuilder -> artBuilder
                .target(Player.class)
                    .action(new PlayerDamageAction())
                .target(Entity.class)
                    .requirement(new EntityLocationRequirement()));
    }

    private void loadARTConfig() {

        if (!isARTLoaded() || getARTManager().isEmpty()) {
            getLogger().warning("ART plugin not found. Not loading ART configs.");
            return;
        }

        Config config = new Config(new File(getDataFolder(), "example.yml"));
        config.loadAndSave();

        setArtResult(ART.load(config.getActions()));
    }

    private boolean isARTLoaded() {
        org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("ART");
        if (plugin == null) {
            return false;
        }

        RegisteredServiceProvider<ArtManager> registration = Bukkit.getServicesManager().getRegistration(ArtManager.class);
        return registration != null;
    }

    private Optional<ArtManager> getARTManager() {

        org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("ART");
        if (plugin == null) {
            return Optional.empty();
        }

        RegisteredServiceProvider<ArtManager> registration = Bukkit.getServicesManager().getRegistration(ArtManager.class);
        if (registration == null) {
            return Optional.empty();
        }

        return Optional.of(registration.getProvider());
    }

    @Getter
    @Setter
    public static class Config extends YamlConfiguration {

        private ArtConfig actions = new ArtConfig();

        protected Config(File file) {
            super(file.toPath());
        }
    }
}
