package io.artframework.bukkit.trigger.configs;

import com.google.common.base.Strings;
import io.artframework.annotations.ConfigOption;
import jdk.jfr.Description;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@Data
public class LocationConfig {

    @ConfigOption(position = 0)
    int x;
    @ConfigOption(position = 1)
    int y;
    @ConfigOption(position = 2)
    int z;
    @ConfigOption(position = 3)
    String world;
    @ConfigOption(position = 4)
    int radius;
    float yaw;
    float pitch;
    @Description("Set to true to check x, y, z, pitch and yaw coordinates that have a value of 0.")
    boolean zeros = false;

    public Location map(Location entityLocation) {

        Location location = new Location(entityLocation.getWorld(), entityLocation.getBlockX(), entityLocation.getBlockY(), entityLocation.getBlockZ(), entityLocation.getYaw(), entityLocation.getPitch());
        if (!Strings.isNullOrEmpty(getWorld())) {
            World world;
            try {
                world = Bukkit.getWorld(UUID.fromString(getWorld()));
            } catch (Exception e) {
                world = Bukkit.getWorld(getWorld());
            }

            if (world != null) location.setWorld(world);
        }
        if (isApplied(getX())) location.setX(getX());
        if (isApplied(getY())) location.setY(getY());
        if (isApplied(getZ())) location.setZ(getZ());
        if (isApplied(getPitch())) location.setPitch(getPitch());
        if (isApplied(getYaw())) location.setYaw(getYaw());

        return location;
    }

    private boolean isApplied(Number value) {
        return (value.floatValue() != 0 || value.intValue() != 0) || isZeros();
    }
}
