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

import net.silthus.art.api.ArtContext;
import net.silthus.art.api.config.ArtConfig;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class BukkitArtResult extends AbstractArtResult {

    BukkitArtResult(ArtConfig config, List<ArtContext<?, ?>> art) {
        super(config, art);
    }

    @Override
    protected <TTarget> boolean filter(TTarget target, ArtContext<TTarget, ?> context) {

        if (target instanceof Entity) {
            List<String> worlds = getConfig().getOptions().getWorlds();
            if (worlds.size() > 0) {
                World world = ((Entity) target).getLocation().getWorld();
                return world == null || worlds.contains(world.getName());
            }
        }

        return false;
    }
}
