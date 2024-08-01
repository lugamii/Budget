package dev.lugami.practice.utils.fake;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.*;

@UtilityClass
public class FakePlayerUtils {

    @Getter
    private static final List<FakePlayer> fakePlayers = new ArrayList<>();

    /**
     * Spawns a fake player, used for testing.
     * Credits to <a href="https://www.spigotmc.org/threads/how-to-create-a-fake-player.181992/">...</a>
     *
     * @param displayName The display name, it'll be used for the fake player.
     * @return The created fake player.
     */
    public FakePlayer spawnFakePlayer(Location loc, String displayName) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        EntityPlayer npc = new EntityPlayer(server, world, new GameProfile(UUID.randomUUID(), displayName), new PlayerInteractManager(world));
        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        CraftPlayer craftPlayer = new CraftPlayer((CraftServer) Bukkit.getServer(), npc);
        FakePlayer fakePlayer = new FakePlayer(craftPlayer);

        for (Player all : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) all).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        }
        
        fakePlayers.add(fakePlayer);
        return fakePlayer;
    }

    /**
     * Removes a fake player based on the display name.
     *
     * @param displayName The display name of the fake player to remove.
     */
    public void removeFakePlayer(String displayName) {
        Iterator<FakePlayer> iterator = fakePlayers.iterator();
        while (iterator.hasNext()) {
            CraftPlayer npc = iterator.next();
            if (npc.getName().equals(displayName)) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    PlayerConnection connection = ((CraftPlayer) all).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc.getHandle()));
                    connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getEntityId()));
                }
                iterator.remove();
                break;
            }
        }
    }

    public FakePlayer getByUUID(UUID uuid) {
        return fakePlayers.stream().filter(cp -> cp.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Updates the position of a fake player.
     *
     * @param fakePlayer The fake player to update.
     * @param newLocation The new location for the fake player.
     */
    public void updateFakePlayerPosition(FakePlayer fakePlayer, Location newLocation) {
        EntityPlayer npc = fakePlayer.getHandle();
        npc.setLocation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(npc);
        for (Player all : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) all).getHandle().playerConnection;
            connection.sendPacket(teleportPacket);
        }
    }

}
