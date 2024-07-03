package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.WorldServer;
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

        tnt.ticksLived = 1;

        Packet<?> packet = new PacketPlayOutSpawnEntity(tnt, 1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
