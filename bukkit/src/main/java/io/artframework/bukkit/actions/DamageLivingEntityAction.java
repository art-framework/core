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

package io.artframework.bukkit.actions;

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

/// [full-example]
/// [header]
// every action, requirement and trigger must be annotated with a @ART annotation.
@ART(
        // Every action needs a unique name across all plugins.
        // It is recommended to prefix it with your plugin name to make sure it is unique.
        value = "art-bukkit:entity.damage",
        // you can define aliases for your ART that will be registered if the alias is not already taken
        alias = {"entity.damage", "damage", "player.damage", "dmg"},
        // You can optionally provide a description about what your action does. I highly recommend to do this.
        description = {
                "Damages the living entity for the given amount of hitpoints.",
                "The entity can also be damaged for a percentage of its maximum or current health."
        }
)
public class DamageLivingEntityAction implements Action<LivingEntity> {
/// [header]
    /// [config]
    // annotate all fields that should be config options with the @ConfigOption annotation
    @ConfigOption(
            description = "Damage amount in percent or health points. Use a value between 0 and 1 if percentage=true.",
            position = 0,
            required = true
    )
    private double amount;

    @ConfigOption(description = "Set to true if you want the entity to be damaged based on his maximum life")
    private boolean percentage = false;

    // all config fields will be exposted as lowercase with underscores
    // so fromCurrent becomes from_current in the config
    // you can override this by providing an explicit config name with @ConfigOption(value = "FROM-CURRENT")
    @ConfigOption(description = "Set to true if you want to damage the entity based on his current health. Only makes sense in combination with percentage=true.")
    private boolean fromCurrent = false;
    /// [config]
    /// [action]
    /**
     * This method will be called everytime your action is executed.
     *
     * @param target the player or other target object your action is executed against
     * @param context context of this action.
     *                Use the context to store data and retrieve additional information about the execution of the action.
     */
    @Override
    public Result execute(@NonNull Target<LivingEntity> target, @NonNull ExecutionContext<ActionContext<LivingEntity>> context) {

        // the target object is always wrapped in a Target<?> class
        // this makes it easy to provide a consistent unique id across different targets
        // use the target.getUniqueId() method if you want to cache something related to the given target instance
        LivingEntity entity = target.source();
        double damage;
        double health = entity.getHealth();

        AttributeInstance attribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) {
            /// [error]
            // you can return an error as the result of your action
            // if something went wrong or is not correctly configured
            return error("Entity " + entity.getName() + "[" + entity.getType().name() + "]" + " does not have a health attribute and cannot be damaged.");
            /// [error]
        }

        double maxHealth = attribute.getValue();

        // here we just do some logic based on the config parameters
        if (this.percentage) {
            if (this.fromCurrent) {
                damage = health * this.amount;
            } else {
                damage = maxHealth * this.amount;
            }
        } else {
            damage = this.amount;
        }

        entity.damage(damage);

        // return a success if your action was successfully executed
        return success();
    }
    /// [action]
}

/// [full-example]