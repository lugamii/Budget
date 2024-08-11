package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public class LightningUtil {

    public void spawnLighting(Player player, Location location) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        net.minecraft.server.v1_8_R3.EntityLightning lightning = new net.minecraft.server.v1_8_R3.EntityLightning(world, location.getX(), location.getY(), location.getZ(), false);
        Packet<?> packet = new PacketPlayOutSpawnEntityWeather(lightning);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
