package dev.lugami.practice.utils;

import dev.lugami.practice.Budget;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public class SoundAPI {

    private final Map<UUID, List<Sound>> playingSounds = new HashMap<>();
    private final CopyOnWriteArrayList<PlayerDisc> players = new CopyOnWriteArrayList<>();

    @RequiredArgsConstructor
    @Getter
    private static class PlayerDisc {

        private final Player player;
        private final Location location;
        private final Disc sound;

    }

    @RequiredArgsConstructor
    @Getter
    public enum Disc {
        GOLD_RECORD(2256, 178),   // "13" music disc
        GREEN_RECORD(2257, 185),  // "Cat" music disc
        RECORD_3(2258, 345),      // "Blocks" music disc
        RECORD_4(2259, 185),      // "Chirp" music disc
        RECORD_5(2260, 174),      // "Far" music disc
        RECORD_6(2261, 197),      // "Mall" music disc
        RECORD_7(2262, 96),      // "Mellohi" music disc
        RECORD_8(2263, 150),      // "Stal" music disc
        RECORD_9(2264, 188),      // "Strad" music disc
        RECORD_10(2265, 251),     // "Ward" music disc
        RECORD_11(2266, 71),     // "11" music disc
        RECORD_12(2267, 238),     // "Wait" music disc
        STOP(0, -1);

        final int id;
        final int lengthInSeconds;

        public static Disc getRandom() {
            Disc disc = values()[new Random().nextInt(values().length)];

            while (disc == RECORD_11 || disc == GOLD_RECORD || disc == STOP) {
                disc = values()[new Random().nextInt(values().length)];
            }

            return disc;
        }

    }

    /**
     * Plays a sound to a player at a specific location.
     *
     * @param player   The player to whom the sound will be played.
     * @param sound    The sound to play.
     * @param location The location where the sound will play.
     */
    public void playSound(Player player, Sound sound, Location location) {
        playSound(player, sound, location, 1.0F, 1.0F);
    }

    /**
     * Plays a sound to a player at a specific location.
     *
     * @param player   The player to whom the sound will be played.
     * @param sound    The sound to play.
     * @param location The location where the sound will play.
     * @param volume   The volume of the sound.
     * @param pitch    The pitch of the sound.
     */
    public void playSound(Player player, Sound sound, Location location, float volume, float pitch) {
        player.playSound(location, sound, volume, pitch);

        playingSounds.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(sound);
        TaskUtil.runTaskLater(() -> {
            List<Sound> sounds = playingSounds.get(player.getUniqueId());
            if (sounds != null) {
                sounds.remove(sound);
                if (sounds.isEmpty()) {
                    playingSounds.remove(player.getUniqueId());
                }
            }
        }, 100L);
    }

    /**
     * Plays a sound to a player at the player's current location.
     *
     * @param player The player to whom the sound will be played.
     * @param sound  The sound to play.
     * @param volume The volume of the sound.
     * @param pitch  The pitch of the sound.
     */
    public void playSound(Player player, Sound sound, float volume, float pitch) {
        playSound(player, sound, player.getLocation(), volume, pitch);
    }

    /**
     * Plays a default Minecraft song to a player.
     *
     * @param player The player to whom the song will be played.
     * @param disc   The disc to play.
     */
    public void playSong(Player player, Disc disc) {
        player.spigot().playEffect(Budget.getInstance().getLobbyStorage().getLobbyLocation(), Effect.RECORD_PLAY, disc.getId(), 0, 0.0F, 0.0F, 0.0F, 1.0F, 1, 256);
        players.add(new PlayerDisc(player, Budget.getInstance().getLobbyStorage().getLobbyLocation(), disc));
    }

    /**
     * Plays a random Minecraft song to a player.
     *
     * @param player The player to whom the song will be played.
     */
    public void playSong(Player player) {
        Disc disc = Disc.getRandom();
        playSong(player, disc);
    }

    /**
     * Stops any currently playing song.
     *
     * @param player The player for whom the song will be stopped.
     */
    public void stopSong(Player player) {
        for (PlayerDisc disc : players) {
            if (disc.player.equals(player)) {
                Location location;
                if (disc.getLocation() != null) {
                    location = disc.getLocation();
                } else {
                    location = Budget.getInstance().getLobbyStorage().getLobbyLocation();
                }

                // #type 1
                float offsetX = 0.0F;
                float offsetY = 0.0F;
                float offsetZ = 0.0F;
                for (int i = 0; i < 5; i++) {
                    player.spigot().playEffect(location, Effect.RECORD_PLAY, Disc.STOP.getId(), 0, offsetX, offsetY, offsetZ, 1.0F, 1, 256);
                    offsetZ = offsetZ + 0.1F;
                    offsetY = offsetY + 0.1F;
                    offsetX = offsetX + 0.1F;
                }

                // #type 2
                PacketDataSerializer packet = new PacketDataSerializer(Unpooled.buffer());

                packet.a("");
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|StopSound", packet));

                players.remove(disc);
            }
        }
    }
}
