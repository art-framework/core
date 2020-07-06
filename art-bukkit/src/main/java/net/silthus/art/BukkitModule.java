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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.silthus.art.api.config.ArtConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BukkitModule extends AbstractModule {

    private final JavaPlugin plugin;

    public BukkitModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    public ArtConfiguration provideConfiguration() {
        ArtConfiguration configuration = new ArtConfiguration(new File(plugin.getDataFolder(), "config.yaml").toPath());
        configuration.loadAndSave();
        return configuration;
    }
}
