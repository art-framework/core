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

package net.silthus.examples.art.requirements;

import net.silthus.art.api.Requirement;
import net.silthus.art.api.annotations.Config;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.requirements.RequirementContext;
import net.silthus.examples.art.configs.LocationConfig;
import org.bukkit.entity.Entity;

@Name("art-example:location")
@Config(LocationConfig.class)
@Description({
        "Checks the position of the entity.",
        "x, y, z, pitch and yaw are ignored if set to 0 unless zeros=true.",
        "Check will always pass if no config is set."
})
public class EntityLocationRequirement implements Requirement<Entity, LocationConfig> {

    @Override
    public boolean test(Entity entity, RequirementContext<Entity, LocationConfig> context) {

        if (context.getConfig().isEmpty()) return true;

        return context.getConfig()
                .map(locationConfig -> locationConfig.isWithinRadius(entity.getLocation()))
                .orElse(true);
    }
}
