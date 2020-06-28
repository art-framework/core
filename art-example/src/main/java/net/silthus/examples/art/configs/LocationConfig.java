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

package net.silthus.examples.art.configs;

import com.google.common.base.Strings;
import lombok.Data;
import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Position;
import net.silthus.examples.art.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Data
public class LocationConfig {

    @Position(0)
    int x;
    @Position(1)
    int y;
    @Position(2)
    int z;
    @Position(3)
    String world;
    @Position(4)
    int radius;
    float yaw;
    float pitch;
    @Description("Set to true to check x, y, z, pitch and yaw coordinates that have a value of 0.")
    boolean zeros = false;

    /**
     * Maps this config to the given location.
     * Replacing all default values with the location values.
     *
     * @param location location to replace default values with
     * @return new location with combined values from the config and the given location
     */
    public Location toLocation(Location location) {
        Location newLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
        if (!Strings.isNullOrEmpty(world)) {
            World world = Bukkit.getWorld(this.world);
            if (world != null) newLocation.setWorld(world);
        }
        if (isApplied(x)) newLocation.setX(x);
        if (isApplied(y)) newLocation.setY(y);
        if (isApplied(z)) newLocation.setZ(z);
        if (isApplied(pitch)) newLocation.setPitch(pitch);
        if (isApplied(yaw)) newLocation.setYaw(yaw);

        return newLocation;
    }

    /**
     * Checks if the given location is within the radius of the location configured by this config.
     *
     * @param location location to check config against
     * @return true if the location is within this configs radius
     */
    public boolean isWithinRadius(Location location) {
        return LocationUtil.isWithinRadius(toLocation(location), location, radius);
    }

    private boolean isApplied(Number value) {
        return (value.floatValue() != 0 || value.intValue() != 0) || zeros;
    }
}
