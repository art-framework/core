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

import io.artframework.ART;
import io.artframework.BootstrapException;
import io.artframework.BootstrapPhase;
import io.artframework.BootstrapScope;
import io.artframework.conf.Settings;
import io.ebean.Database;
import kr.entree.spigradle.annotations.Plugin;
import lombok.Getter;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@Plugin
public class ArtBukkitPlugin extends JavaPlugin {

    @Getter
    private static boolean testing = false;

    private BootstrapPhase bootstrap;

    public ArtBukkitPlugin() {
    }

    public ArtBukkitPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        testing = true;
    }

    @Override
    public void onLoad() {

        try {
            bootstrap = ART.bootstrap(BootstrapScope.of(new ArtBukkitModule(this), Settings.builder()
                    .basePath(getDataFolder().getAbsolutePath())
                    .build()
            ), true);
        } catch (BootstrapException e) {
            getLogger().severe("failed to bootstrap the art-framework: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {

        getDataFolder().mkdirs();

        if (bootstrap != null) {
            bootstrap.loadAll();

            Bukkit.getScheduler().runTaskLater(this, () -> bootstrap.enableAll(), 1L);
        }
    }
}
