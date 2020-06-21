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

package net.silthus.examples.art.requirements;

import com.google.common.base.Strings;
import net.silthus.art.RequirementContext;
import net.silthus.art.api.annotations.Config;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Name;
import net.silthus.art.api.annotations.Position;
import net.silthus.art.api.requirements.Requirement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

@Name("art-example:location")
@Config(EntityLocationRequirement.Config.class)
@Description({
        "Checks the position of the entity.",
        "x, y, z, pitch and yaw are ignored if set to 0 unless zeros=true.",
        "Check will always pass if no config is set."
})
public class EntityLocationRequirement implements Requirement<Entity, EntityLocationRequirement.Config> {

    @Override
    public boolean test(Entity entity, RequirementContext<Entity, Config> context) {

        if (context.getConfig().isEmpty()) return true;

        Config config = context.getConfig().get();

        Location entityLocation = entity.getLocation();
        Location configLocation = toLocation(config, entityLocation);

        return isWithinRadius(configLocation, entityLocation, config.radius);
    }

    private boolean isApplied(Config config, Number value) {
        return ((value.floatValue() != 0 || value.intValue() != 0) || config.zeros) && config.radius < 1;
    }

    private Location toLocation(Config config, Location entityLocation) {
        Location location = new Location(entityLocation.getWorld(), entityLocation.getBlockX(), entityLocation.getBlockY(), entityLocation.getBlockZ(), entityLocation.getYaw(), entityLocation.getPitch());
        if (!Strings.isNullOrEmpty(config.world)) {
            World world = Bukkit.getWorld(config.world);
            if (world != null) location.setWorld(world);
        }
        if (isApplied(config, config.x)) location.setX(config.x);
        if (isApplied(config, config.y)) location.setY(config.y);
        if (isApplied(config, config.z)) location.setZ(config.z);
        if (isApplied(config, config.pitch)) location.setPitch(config.pitch);
        if (isApplied(config, config.yaw)) location.setYaw(config.yaw);

        return location;
    }

    public static class Config {

        @Position(0)
        private int x;
        @Position(1)
        private int y;
        @Position(2)
        private int z;
        @Position(3)
        private String world;
        @Position(4)
        private int radius;
        private float yaw;
        private float pitch;
        @Description("Set to true to check x, y, z, pitch and yaw coordinates that have a value of 0.")
        private final boolean zeros = false;
    }

    // TODO: move all static to util class
    public static boolean isWithinRadius(Location l1, Location l2, int radius) {

        if (l1.getWorld() != null && l2.getWorld() != null) {
            return l1.getWorld().equals(l2.getWorld()) && getDistanceSquared(l1,
                    l2) <= radius * radius;
        }
        return false;
    }

    /**
     * Gets the distance between two points.
     *
     * @param l1
     * @param l2
     *
     * @return
     */
    public static double getDistance(Location l1, Location l2) {

        return getBlockDistance(l1, l2);
    }

    public static double getDistanceSquared(Location l1, Location l2) {

        return getBlockDistance(l1, l2) * getBlockDistance(l1, l2);
    }

    /**
     * Gets the greatest distance between two locations. Only takes
     * int locations and does not check a round radius.
     *
     * @param l1 to compare
     * @param l2 to compare
     *
     * @return greatest distance
     */
    public static int getBlockDistance(Location l1, Location l2) {

        int x = Math.abs(l1.getBlockX() - l2.getBlockX());
        int y = Math.abs(l1.getBlockY() - l2.getBlockY());
        int z = Math.abs(l1.getBlockZ() - l2.getBlockZ());
        if (x >= y && x >= z) {
            return x;
        } else if (y >= x && y >= z) {
            return y;
        } else if (z >= x && z >= y) {
            return z;
        } else {
            return x;
        }
    }

    /**
     * @return the correct distance between blocks without y layer
     */
    public static double getRealDistance(double x1, double z1, double x2, double z2) {

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(z2 - z1);
        return Math.sqrt(dx * dx + dy * dy);
    }
}
