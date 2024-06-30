package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public class LightningUtil {
    
    public void spawnLighting(Player player, Location location) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityLightning lightning = new EntityLightning(worldServer, location.getX(), location.getY(), location.getZ(), false);

        PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(lightning);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
