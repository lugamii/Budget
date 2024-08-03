package dev.lugami.practice.utils;

import com.mojang.authlib.GameProfile;
import dev.lugami.practice.Budget;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.github.paperspigot.Title;

import java.lang.reflect.Field;
import java.util.UUID;

@UtilityClass
public class PlayerUtils {

    /**
     * Resets a player's state to the default.
     *
     * @param player The player to reset.
     */
    public void resetPlayer(Player player) {
        resetPlayer(player, true);
    }

    /**
     * Resets a player's state to the default.
     *
     * @param player The player to reset.
     * @param show   If we should show the player after resetting him or
     *               keep his visibility state.
     */
    public void resetPlayer(Player player, boolean show) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20f);
        for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
        player.setExp(0);
        player.setLevel(0);
        player.setFireTicks(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFallDistance(0);
        player.setExhaustion(0);
        player.setMaximumNoDamageTicks(20);
        player.setNoDamageTicks(0);
        player.setRemainingAir(player.getMaximumAir());
        player.setCanPickupItems(true);
        if (show) showPlayer(player);
    }

    /**
     * Helps the player to enter on spectator mode.
     */
    public void joinSpectator(Player player) {
        TaskUtil.runTask(() -> {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setGameMode(GameMode.CREATIVE);
            player.setFlySpeed(0.2F);
            player.updateInventory();
        });
    }

    /**
     * Gets the ping of a player.
     *
     * @param player The player to get the ping of.
     * @return The player's ping in milliseconds.
     */
    public int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Field pingField = entityPlayer.getClass().getDeclaredField("ping");
            pingField.setAccessible(true);
            return pingField.getInt(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Sets the last attacker for the specified victim player.
     * Credits: Praxi (joeleoli)
     *
     * @param victim   The player who was attacked.
     * @param attacker The player who performed the attack.
     */
    public void setLastAttacker(Player victim, Player attacker) {
        victim.setMetadata("lastAttacker", new FixedMetadataValue(Budget.getInstance(), attacker.getUniqueId()));
    }

    /**
     * Retrieves the last attacker for the specified victim player.
     * Credits: Praxi (joeleoli)
     *
     * @param victim The player whose last attacker is to be retrieved.
     * @return The last attacker player, or null if not set.
     */
    public Player getLastAttacker(Player victim) {
        if (victim.hasMetadata("lastAttacker")) {
            return Bukkit.getPlayer((UUID) victim.getMetadata("lastAttacker").get(0).value());
        } else {
            return null;
        }
    }

    /**
     * Respawns a given player.
     *
     * @param player The player to be respawned
     */
    public void respawnPlayer(Player player) {
        if (player.isDead()) {
            ((CraftPlayer) player).getHandle().playerConnection.a(
                    new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN)
            );
        }
    }

    /**
     * Hides a player from all other players.
     *
     * @param player The player to hide.
     */
    public void hidePlayer(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                other.hidePlayer(player);
                Budget.getInstance().getEntityHider().hideEntity(player, other);
            }
        }
    }

    /**
     * Shows a player to all other players.
     *
     * @param player The player to show.
     */
    public void showPlayer(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                other.showPlayer(player);
                Budget.getInstance().getEntityHider().showEntity(player, other);
            }
        }
    }

    /**
     * Returns if a player is a developer of Budget.
     *
     * @param player The player to check.
     */
    public boolean isDev(Player player) {
        return player.getUniqueId().toString().equalsIgnoreCase("e0cf080f-46a9-4297-b2cf-eeb2a1ea06e9") || player.getUniqueId().toString().equalsIgnoreCase("b75c5e80-d61f-403b-bd83-176e8f8ef19b") || player.getUniqueId().toString().equalsIgnoreCase("518f5457-5e6b-4314-b95a-c2ce831f9f8b");
    }
}
