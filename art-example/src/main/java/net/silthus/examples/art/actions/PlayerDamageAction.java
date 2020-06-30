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

package net.silthus.examples.art.actions;

import lombok.NonNull;
import net.silthus.art.api.Action;
import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.annotations.*;
import net.silthus.art.api.trigger.Target;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

/**
 * Every action needs a unique name across all plugins.
 * It is recommended to prefix it with your plugin name to make sure it is unique.
 *
 * The @Name annotation is required on all actions or else the registration will fail.
 *
 * You can optionally provide a @Config that will be used to describe the parameter your action takes.
 */
@Name("art-example:player.damage")
@Config(PlayerDamageAction.ActionConfig.class)
public class PlayerDamageAction implements Action<Player, PlayerDamageAction.ActionConfig> {

    /**
     * This method will be called everytime your action is executed.
     *
     * @param target  the player or other target object your action is executed against
     * @param context context of this action.
     *                Use the {@link ActionContext} to retrieve the config
     */
    @Override
    public void execute(Target<Player> target, ActionContext<Player, ActionConfig> context) {
        context.getConfig().ifPresent(config -> {
            @NonNull Player player = target.getSource();
            double damage;
            double health = player.getHealth();
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            double maxHealth = attribute == null ? health : attribute.getValue();

            if (config.percentage) {
                if (config.fromCurrent) {
                    damage = health * config.amount;
                } else {
                    damage = maxHealth * config.amount;
                }
            } else {
                damage = config.amount;
            }

            player.damage(damage);
        });
    }

    /**
     * You should annotate all of your config parameters with a @Description.
     * This will make it easier for the admins to decide what to configure.
     *
     * You can also tag config fields with a @Required flag.
     * The action caller will get an error if the parameter is not defined inside the config.
     *
     * Additionally to that you have to option to mark your parameters with the @Position position.
     * Start in an indexed manner at 0 and count upwards. This is optional.
     *
     * This means your action can be called like this: !art-example:player.damage 10
     * instead of: !art-example:player.damage amount=10
     */
    public static class ActionConfig {

        @Required
        @Position(0)
        @Description("Damage amount in percent or health points. Use a value between 0 and 1 if percentage=true.")
        private double amount;

        @Description("Set to true if you want the player to be damaged based on his maximum life")
        private final boolean percentage = false;

        @Description("Set to true if you want to damage the player based on his current health. Only makes sense in combination with percentage=true.")
        private final boolean fromCurrent = false;
    }
}
