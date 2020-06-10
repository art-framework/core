package net.silthus.art.bukkit.trigger;

import net.silthus.art.api.annotations.Configurable;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.trigger.Trigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveTrigger extends Trigger<Player, Location> implements Listener {

    @Name("player.move")
    @Configurable("[Location]location: takes a bukkit location object or single coordinates in the format x,y,z,yaw,pitch,world,radius")
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        trigger("player.move", event.getPlayer(), context ->
                !context.getConfig().isPresent()
                        || context.getConfig().get().equals(event.getTo())
        );

        // alternative from any class, but must be registered manually
//        ART.<Player, Location>trigger("player.move", event.getPlayer(), context ->
//                !context.getConfig().isPresent()
//              || context.getConfig().get().equals(event.getTo())
//        );
    }
}
