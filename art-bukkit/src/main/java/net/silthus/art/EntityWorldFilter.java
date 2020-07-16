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

import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class EntityWorldFilter implements java.util.function.Predicate<Target<Entity>> {

    @Override
    public boolean test(Target<Entity> entity, ArtConfig config) {
        List<String> worlds = config.getOptions().getWorlds();
        if (worlds.size() > 0) {
            World world = entity.getSource().getLocation().getWorld();
            return world == null || worlds.contains(world.getName());
        }
        return true;
    }
}
