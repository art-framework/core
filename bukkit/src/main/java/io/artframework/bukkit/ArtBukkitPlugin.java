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
import io.artframework.BootstrapScope;
import io.artframework.conf.Settings;
import kr.entree.spigradle.annotations.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Plugin
public class ArtBukkitPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        getDataFolder().mkdirs();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            try {
                ART.bootstrap(BootstrapScope.of(new ArtBukkitModule(this), Settings.builder()
                        .basePath(getDataFolder().getAbsolutePath())
                        .build()
                ), true);
            } catch (BootstrapException e) {
                e.printStackTrace();
            }
        }, 1L);
    }
}
