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

package net.silthus.examples.art.trigger;

import net.silthus.art.api.Trigger;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.trigger.TriggerContext;
import net.silthus.examples.art.configs.LocationConfig;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Predicate;

public class PlayerMoveTrigger implements Trigger, Listener {

    private static final String PLAYER_MOVE = "art-example:player.move";
    private static final String CHEST_OPEN = "art-example:chest.open";

    @Name(PLAYER_MOVE)
    @Description({
            "Triggers if the player moved to the given location.",
            "Will only check full block moves and not every rotation of the player."
    })
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!hasMoved(event)) return;

        trigger(PLAYER_MOVE, test(event.getTo()), event.getPlayer());
    }

    // example for reusing the same config
    // here we are triggering the event for two targets: the chest and the player
    @Name(CHEST_OPEN)
    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {

        trigger(CHEST_OPEN, test(event.getInventory().getLocation()), event.getPlayer(), event.getInventory());
    }

    private Predicate<TriggerContext<LocationConfig>> test(Location location) {
        return context -> {
            if (location == null) {
                return true;
            }
            return context.getConfig()
                    .map(locationConfig -> locationConfig.isWithinRadius(location))
                    .orElse(true);
        };
    }

    private boolean hasMoved(PlayerMoveEvent event) {
        if (event.getTo() == null) return false;

        return event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }
}
