package dev.lugami.practice.protocol;

import com.comphenix.tinyprotocol.TinyProtocol;
import dev.lugami.practice.Budget;
import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A custom EntityHider that uses TinyProtocol instead of ProtocolLib.
 * Took a while to fix, but I think it works.
 */
public class EntityHider extends TinyProtocol {

    private final Map<UUID, Set<UUID>> hiddenEntities = new ConcurrentHashMap<>();

    public EntityHider() {
        super(Budget.getInstance());
    }

    public void hideEntity(Player player, Player toHide) {
        player.hidePlayer(toHide);
        hiddenEntities.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(toHide.getUniqueId());
    }

    public void showEntity(Player player, Player toShow) {
        player.showPlayer(toShow);
        Set<UUID> hidden = hiddenEntities.get(player.getUniqueId());
        if (hidden != null) {
            hidden.remove(toShow.getUniqueId());
            if (hidden.isEmpty()) {
                hiddenEntities.remove(player.getUniqueId());
            }
        }
    }

    @Override
    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
        try {
            if (receiver == null || receiver.getUniqueId() == null) return super.onPacketOutAsync(receiver, channel, packet);
            UUID receiverId = receiver.getUniqueId();
            if (!hiddenEntities.containsKey(receiverId)) return super.onPacketOutAsync(receiver, channel, packet);

            int entityId = getEntityIdFromPacket(packet);
            if (entityId != -1) {
                Player toHide = getPlayerByEntityId(entityId);
                if (toHide != null && hiddenEntities.get(receiverId).contains(toHide.getUniqueId())) {
                    return null;
                }
            }

            return super.onPacketOutAsync(receiver, channel, packet);
        } catch (Exception e) {
            e.printStackTrace();
            return super.onPacketOutAsync(receiver, channel, packet);
        }
    }

    private int getEntityIdFromPacket(Object packet) {
        try {
            if (packet instanceof PacketPlayOutEntity) {
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
            } else if (packet instanceof PacketPlayOutEntityVelocity) {
                Field field = PacketPlayOutEntityVelocity.class.getDeclaredField("a");
                field.setAccessible(true);
                return field.getInt(packet);
            } else if (packet instanceof PacketPlayOutSpawnEntity) {
                Field field = PacketPlayOutSpawnEntity.class.getDeclaredField("a");
                field.setAccessible(true);
                return field.getInt(packet);
            } else if (packet instanceof PacketPlayOutSpawnEntityLiving) {
                Field field = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("a");
                field.setAccessible(true);
                return field.getInt(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Player getPlayerByEntityId(int entityId) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getEntityId() == entityId)
                .findFirst()
                .orElse(null);
    }
}
