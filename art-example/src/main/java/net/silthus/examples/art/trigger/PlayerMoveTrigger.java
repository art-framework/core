package net.silthus.examples.art.trigger;

import de.exlll.configlib.annotation.ConfigurationElement;
import lombok.Data;
import net.silthus.art.api.annotations.Configurable;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.trigger.Trigger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveTrigger extends Trigger<Player, PlayerMoveTrigger.LocationConfig> implements Listener {

    @Name("player.move")
    @Configurable("[Location]location: takes a bukkit location object or single coordinates in the format x,y,z,yaw,pitch,world,radius")
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        trigger("player.move", event.getPlayer(), context ->
                // TODO: create real check
                context.getConfig().canEqual(event.getTo())
        );
    }

    @Data
    @ConfigurationElement
    public static class LocationConfig {
        private int x;
        private int y;
        private int z;
        private float pitch;
        private float yaw;
        private String world;
        private int radius;
    }
}
