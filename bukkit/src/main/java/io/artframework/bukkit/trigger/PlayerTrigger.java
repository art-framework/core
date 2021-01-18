package io.artframework.bukkit.trigger;

import io.artframework.Trigger;
import io.artframework.annotations.ART;
import io.artframework.bukkit.trigger.configs.LocationConfig;
import io.artframework.bukkit.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTrigger implements Listener, Trigger {

    public static final String PLAYER_MOVE = "player.move";

    private final Map<UUID, Location> lastLocations = new HashMap<>();

    @ART(value = PLAYER_MOVE, alias = {"move", "loc", "pos", "location", "player.location"}, description = "Triggers if the player enters the given location.")
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {

        Location lastLocation = lastLocations.computeIfAbsent(event.getPlayer().getUniqueId(), uuid -> event.getTo());
        if (!moved(lastLocation, event.getTo())) return;
        lastLocations.put(event.getPlayer().getUniqueId(), event.getTo());

        trigger(PLAYER_MOVE, of(event.getPlayer(), LocationConfig.class, (target, context, locationConfig) -> {
            Location configLocation = locationConfig.map(target.source().getLocation());
            return resultOf(LocationUtil.isWithinRadius(configLocation, target.source().getLocation(), locationConfig.getRadius()));
        }));
    }

    private boolean moved(Location from, Location to) {

        if (from == null) return true;

        return from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();
    }
}
