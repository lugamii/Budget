package dev.lugami.practice.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    /**
     * Serialize a location to a string.
     *
     * @param location the location to serialize
     * @return the serialized string representation of the location
     */
    public static String locationToString(Location location) {
        if (location == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(location.getWorld().getName()).append(":");
        builder.append(location.getX()).append(":");
        builder.append(location.getY()).append(":");
        builder.append(location.getZ()).append(":");
        builder.append(location.getYaw()).append(":");
        builder.append(location.getPitch());
        return builder.toString();
    }

    /**
     * Deserialize a string to a location.
     *
     * @param str the string representation of the location
     * @return the Location object, or null if the string is invalid or the world does not exist
     */
    public static Location stringToLocation(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        String[] parts = str.split(":");
        if (parts.length != 6) {
            return null;
        }
        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null; // World does not exist
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}
