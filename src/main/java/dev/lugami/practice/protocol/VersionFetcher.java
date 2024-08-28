package dev.lugami.practice.protocol;

import com.comphenix.tinyprotocol.TinyProtocol;
import dev.lugami.practice.Budget;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketHandshakingInSetProtocol;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutKickDisconnect;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A mini version fetcher for players.
 * Whenever they join, it'll catch the player's version, and save it to this class
 */
public class VersionFetcher extends TinyProtocol {

    private final Map<UUID, Integer> versionMap = new ConcurrentHashMap<>();

    public VersionFetcher() {
        super(Budget.getInstance());
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        if (sender == null || sender.getUniqueId() == null) return super.onPacketInAsync(sender, channel, packet);

        if (packet instanceof PacketHandshakingInSetProtocol) {
            int protocolVersion = ((PacketHandshakingInSetProtocol) packet).b();
            this.versionMap.put(sender.getUniqueId(), protocolVersion);

            Version playerVersion = this.getVersion(sender);
            if (playerVersion != null) {
                Budget.getInstance().getLogger().info(sender.getName() + " joined with version " + playerVersion.name());
            } else {
                Budget.getInstance().getLogger().warning(sender.getName() + " joined with an unknown version (" + protocolVersion + ")");
            }
        }

        return super.onPacketInAsync(sender, channel, packet);
    }

    @Override
    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
        if (receiver == null || receiver.getUniqueId() == null) return super.onPacketOutAsync(receiver, channel, packet);

        if (packet instanceof PacketPlayOutKickDisconnect) {
            this.versionMap.remove(receiver.getUniqueId());
        }

        return super.onPacketOutAsync(receiver, channel, packet);
    }

    public Version getVersion(Player player) {
        Integer version = this.versionMap.get(player.getUniqueId());
        if (version != null) {
            for (Version v : Version.values()) {
                if (v.getVersion() == version) {
                    return v;
                }
            }
        }
        return null;
    }


    @AllArgsConstructor
    @Getter
    public enum Version {
        v1_7(4),
        v1_7_2(5),
        v1_7_6(47),
        v1_8(47),
        v1_9(107),
        v1_9_1(108),
        v1_9_2(109),
        v1_9_3(110),
        v1_10(210),
        v1_11(315),
        v1_11_1(316),
        v1_12(335),
        v1_12_1(338),
        v1_12_2(340),
        v1_13(393),
        v1_13_1(401),
        v1_13_2(404),
        v1_14(477),
        v1_14_1(480),
        v1_14_2(485),
        v1_14_3(490),
        v1_14_4(498),
        v1_15(573),
        v1_15_1(575),
        v1_15_2(578),
        v1_16(735),
        v1_16_1(736),
        v1_16_2(751),
        v1_16_3(753),
        v1_16_4(754),
        v1_17(755),
        v1_17_1(756),
        v1_18(757),
        v1_18_1(758),
        v1_18_2(759),
        v1_19(760),
        v1_19_1(761),
        v1_19_2(762),
        v1_19_3(763),
        v1_19_4(764),
        v1_20(765),
        v1_20_1(766);

        final int version;
    }
}
