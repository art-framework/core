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

package net.silthus.art.storage.hibernate;

import com.google.inject.Binder;
import kr.entree.spigradle.annotations.PluginMain;
import net.silthus.slib.bukkit.BasePlugin;

@PluginMain
public class ArtHibernatePlugin extends BasePlugin {

    @Override
    public void enable() {

        getLogger().info("Registered Hibernate as ART Storage Provider. Use it by setting \"storage_provider: hibernate\" in your config.yaml.");
    }

    @Override
    public void disable() {

    }

    @Override
    public void configure(Binder binder) {
        binder.install(new HibernateModule());
    }
}
