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

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.bukkit.trigger.configs.EntityDamageConfig;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@ART(
        value = "entity.damage",
        alias = {"damage", "player.damage", "dmg"},
        description = {
                "The trigger is fired when an entity or player takes damage.",
                "You can use the bukkit:event.cancel action to cancel the damage event."
        }
)
public class EntityDamageTrigger implements Trigger, Listener, Configurable<EntityDamageConfig>, Requirement<EntityDamageEvent> {

    private final Scope scope;
    private EntityDamageConfig config;

    public EntityDamageTrigger(Scope scope) {
        this.scope = scope;
    }

    @Override
    public void load(@NonNull EntityDamageConfig entityDamageConfig) {

        this.config = entityDamageConfig;
    }

    @Override
    public Result test(@NonNull Target<EntityDamageEvent> target, @NonNull ExecutionContext<RequirementContext<EntityDamageEvent>> context) {

        return resultOf(config.isApplicable(target.source()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        scope.trigger(EntityDamageTrigger.class)
                .with(event)
                .with(event.getEntity())
                .execute();
    }
}
