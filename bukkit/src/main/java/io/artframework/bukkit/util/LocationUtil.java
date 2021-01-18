package io.artframework.bukkit.util;

import org.bukkit.Location;

public final class LocationUtil {

    private LocationUtil() {}

    public static boolean isWithinRadius(Location l1, Location l2, int radius) {

        if (l1.getWorld() != null && l2.getWorld() != null) {
            if (radius <= 0) {
                return l1.getBlockX() == l2.getBlockX()
                        && l1.getBlockY() == l2.getBlockY()
                        && l1.getBlockZ() == l2.getBlockZ();
            } else {
                return l1.getWorld().equals(l2.getWorld()) && getDistanceSquared(l1,
                        l2) <= radius * radius;
            }
        }
        return false;
    }

    /**
     * Gets the distance between two points.
     *
     * @param l1 location one
     * @param l2 location two
     *
     * @return distance in blocks between the two points
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
        } else {
            return z;
        }
    }

    public static double getRealDistance(double x1, double z1, double x2, double z2) {

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(z2 - z1);
        return Math.sqrt(dx * dx + dy * dy);
    }
}
