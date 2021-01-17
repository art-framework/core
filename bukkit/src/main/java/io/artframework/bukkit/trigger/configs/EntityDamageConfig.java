/*
 *  Copyright 2020 ART-Framework Contributors (https://github.com/art-framework/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.artframework.bukkit.trigger.configs;

import com.google.common.base.Strings;
import io.artframework.annotations.ConfigOption;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

@ConfigOption
public class EntityDamageConfig {

//    @ConfigOption(
//            description = {
//                    "An expression of the damage the entity received.",
//                    "For example: >0 or 10..20 (between 10 and 20 damage received)",
//                    "The expression can be an absolute value as well or an percentage, e.g. 20% (of the max health)"
//            },
//            position = 0
//    )
//    private String damage = ">0";

    @ConfigOption(description = "Define the targets the event should fire for. Allowed values or PLAYER and ENTITY.", position = 0)
    private String target = "PLAYER";
    @ConfigOption(description = {"Use the setting to define the required minimum damage.", "-1 will disable the setting."})
    private double minDamage = -1;
    @ConfigOption(description = {"Use the setting to define the required maximum damage.", "-1 will disable the setting."})
    private double maxDamage = -1;

    @ConfigOption(
            description = "Set this to false to use the raw damage of the event and not the damage after all reductions have been applied."
    )
    private boolean useFinalDamage = true;

    public boolean isApplicable(EntityDamageEvent event) {

        if (target.equalsIgnoreCase("player")) {
            if (!(event.getEntity() instanceof Player)) {
                return false;
            }
        } else if (target.equalsIgnoreCase("entity")) {
            if (event.getEntity() instanceof Player) {
                return false;
            }
        }

        double damage = useFinalDamage ? event.getFinalDamage() : event.getDamage();

        if (minDamage > -1 && damage < minDamage) {
            return false;
        } else return !(maxDamage > -1) || !(damage > maxDamage);
    }
}
