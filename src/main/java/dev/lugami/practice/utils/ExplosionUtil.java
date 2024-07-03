package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public class ExplosionUtil {

    public void spawnExplosion(Player player, Location location) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityTNTPrimed tnt = new EntityTNTPrimed(worldServer);

        tnt.setPosition(location.getX(), location.getY(), location.getZ());
        tnt.fuseTicks = 50;
        worldServer.addEntity(tnt);

        Packet<?> packetSpawn = new PacketPlayOutSpawnEntity(tnt, 50);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSpawn);
    }
}