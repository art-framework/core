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

package io.artframework.bukkit.trigger;

import io.artframework.Trigger;
import io.artframework.TriggerRequirement;
import io.artframework.annotations.ART;
import io.artframework.bukkit.trigger.configs.EntityDamageConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityTrigger implements Trigger, Listener {

    private static final String ENTITY_DAMAGE = "entity.damage";

    @ART(
            value = ENTITY_DAMAGE,
            alias = {"damage", "player.damage", "dmg"},
            description = {
                    "The trigger is fired when an entity or player takes damage.",
                    "You can use the bukkit:event.cancel action to cancel the damage event."
            }
    )
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        TriggerRequirement<Object, EntityDamageConfig> requirement =
                (target, context, damageConfig) -> resultOf(damageConfig.isApplicable(event));

        trigger(ENTITY_DAMAGE,
                of(event, EntityDamageConfig.class, requirement),
                of(event.getEntity(), EntityDamageConfig.class, requirement)
        );
    }
}
