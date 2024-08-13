package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public class LightningUtil {

    /**
     * Spawns a lightning strike on the given player and given location.
     * NOTE: THIS IS AN ALTERNATIVE TO ProtocolLib, AS BUDGET
     * CURRENTLY AIMS TO AVOID USING IT (to make it almost dependless)
     *
     * @param player The given player
     * @param location The location to spawn the lightning
     */
    public void spawnLightning(Player player, Location location) {
        TaskUtil.runTaskLater(() -> {
            try {
                World world = ((CraftWorld) location.getWorld()).getHandle();
                EntityLightning lightning = new EntityLightning(world, location.getX(), location.getY(), location.getZ(), false);
                PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(lightning);
                PacketPlayOutEntityStatus packet2 = new PacketPlayOutEntityStatus(((CraftPlayer) player).getHandle(), (byte) 3);
                Bukkit.getOnlinePlayers().forEach(player1 -> {
                    CraftPlayer p = (CraftPlayer) player1;
                    p.getHandle().playerConnection.sendPacket(packet);
                    if (!player1.equals(player)) {
                        p.getHandle().playerConnection.sendPacket(packet2);
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 1L);
    }
}
