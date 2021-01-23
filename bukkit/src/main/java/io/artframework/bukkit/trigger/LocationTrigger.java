package io.artframework.bukkit.trigger;

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.bukkit.trigger.configs.LocationConfig;
import io.artframework.bukkit.util.LocationUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ART(
        value = "player.move",
        alias = {"move", "loc", "pos", "location", "player.location"},
        description = "Checks the player position against the given coordinates and radius."
)
public class LocationTrigger implements Listener, Trigger, Requirement<Entity>, Configurable<LocationConfig>, Scoped {

    @Getter
    @Accessors(fluent = true)
    private final Scope scope;
    private final Map<UUID, Location> lastLocations = new HashMap<>();

    private LocationConfig config;

    public LocationTrigger(Scope scope) {
        this.scope = scope;
    }

    @Override
    public void load(@NonNull LocationConfig locationConfig) {

        this.config = locationConfig;
    }

    @Override
    public Result test(@NonNull Target<Entity> target, @NonNull ExecutionContext<RequirementContext<Entity>> context) {

        Location configLocation = config.map(target.source().getLocation());
        return resultOf(LocationUtil.isWithinRadius(configLocation, target.source().getLocation(), config.getRadius()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {

        Location lastLocation = lastLocations.computeIfAbsent(event.getPlayer().getUniqueId(), uuid -> event.getTo());
        if (!moved(lastLocation, event.getTo())) return;
        lastLocations.put(event.getPlayer().getUniqueId(), event.getTo());

        scope().trigger(LocationTrigger.class)
                .with(event.getPlayer())
                .execute();
    }

    private boolean moved(Location from, Location to) {

        if (from == null) return true;

        return from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();
    }
}
