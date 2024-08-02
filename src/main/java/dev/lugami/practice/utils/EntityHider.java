package dev.lugami.practice.utils;

import com.comphenix.tinyprotocol.TinyProtocol;
import dev.lugami.practice.Budget;
import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A custom EntityHider that only uses TinyProtocol.
 * Although not tested, should work fine.
 */
public class EntityHider extends TinyProtocol {

    private final Map<UUID, Set<UUID>> hiddenEntities = new HashMap<>();

    public EntityHider() {
        super(Budget.getInstance());
    }

    public void hideEntity(Player player, Player toHide) {
        this.hiddenEntities.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(toHide.getUniqueId());
        this.sendDestroyPacket(player, toHide);
    }

    public void showEntity(Player player, Player toShow) {
        Set<UUID> hidden = hiddenEntities.get(player.getUniqueId());
        if (hidden != null) {
            hidden.remove(toShow.getUniqueId());
        }
    }

    private void sendDestroyPacket(Player player, Player toHide) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(toHide.getEntityId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
        if (receiver == null || receiver.getUniqueId() == null) return super.onPacketOutAsync(receiver, channel, packet);
        UUID receiverId = receiver.getUniqueId();

        if (!this.hiddenEntities.containsKey(receiverId)) {
            return super.onPacketOutAsync(receiver, channel, packet);
        }

        int entityId = this.getEntityIdFromPacket(packet);
        if (entityId != -1) {
            Player toHide = this.getPlayerByEntityId(entityId);
            if (toHide != null && this.hiddenEntities.get(receiverId).contains(toHide.getUniqueId())) {
                return null;
            }
        }

        return super.onPacketOutAsync(receiver, channel, packet);
    }

    private int getEntityIdFromPacket(Object packet) {
        try {
            if (packet instanceof PacketPlayOutSpawnEntity) {
                Field field = PacketPlayOutSpawnEntity.class.getDeclaredField("a");
                field.setAccessible(true);
                return field.getInt(packet);
            } else if (packet instanceof PacketPlayOutEntity) {
                Field field = PacketPlayOutEntity.class.getDeclaredField("a");
                field.setAccessible(true);
                return field.getInt(packet);
            } else if (packet instanceof PacketPlayOutEntityEffect) {
                Field field = PacketPlayOutEntityEffect.class.getDeclaredField("a");
                field.setAccessible(true);
                return field.getInt(packet);
            } else if (packet instanceof PacketPlayOutEntityStatus) {
                Field field = PacketPlayOutEntityStatus.class.getDeclaredField("a");
                field.setAccessible(true);
                return field.getInt(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Player getPlayerByEntityId(int entityId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getEntityId() == entityId) {
                return player;
            }
        }
        return null;
    }
}
