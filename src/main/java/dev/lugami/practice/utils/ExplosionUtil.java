package dev.lugami.practice.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ExplosionUtil {

    @Getter
    private final List<EntityTNTPrimed> spawned = new ArrayList<>();

    public void spawnExplosion(Player player, Location location) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityTNTPrimed tnt = new EntityTNTPrimed(worldServer);

        tnt.setPosition(location.getX(), location.getY(), location.getZ());
        tnt.fuseTicks = 50;
        worldServer.addEntity(tnt);
        spawned.add(tnt);

        Packet<?> packetSpawn = new PacketPlayOutSpawnEntity(tnt, 50);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSpawn);
    }
}