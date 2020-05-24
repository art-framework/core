package net.silthus.art.bukkit.trigger;

import net.silthus.art.api.trigger.Trigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class PlayerMoveTrigger extends Trigger<Player, Location> implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        trigger(event.getPlayer(), context -> Objects.equals(event.getTo(), context.getConfig().orElse(null)));
    }
}
