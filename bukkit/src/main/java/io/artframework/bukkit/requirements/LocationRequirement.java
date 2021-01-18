package io.artframework.bukkit.requirements;

import com.google.common.base.Strings;
import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.bukkit.trigger.configs.LocationConfig;
import io.artframework.bukkit.util.LocationUtil;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

@ART(value = "location", alias = {"loc", "pos"}, description = "Checks the player position against the given coordinates and radius.")
public class LocationRequirement implements Requirement<Entity>, Configurable<LocationConfig> {

    LocationConfig config;

    @Override
    public void load(@NonNull LocationConfig locationConfig) {

        config = locationConfig;
    }

    @Override
    public Result test(@NonNull Target<Entity> target, @NonNull ExecutionContext<RequirementContext<Entity>> context) {

        if (config == null) return success();

        Location entityLocation = target.source().getLocation();
        Location configLocation = config.map(entityLocation);

        return resultOf(LocationUtil.isWithinRadius(configLocation, entityLocation, config.getRadius()));
    }
}
