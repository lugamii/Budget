package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.settings.SettingsChangedEvent;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.PlayerUtils;
import dev.lugami.practice.utils.SoundAPI;
import dev.lugami.practice.utils.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        if (Budget.getInstance().getProfileStorage().findProfile(player) != null) return;
        Profile profile = new Profile(player);
        TaskUtil.runTaskLater(() -> {
            if (Budget.getInstance().getMainConfig().getBoolean("debug")) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    if (!player1.isOp()) return;
                    player1.sendMessage(CC.translate("&7&oDEBUG: " + profile.getPlayer().getName() + "'s profile was setup successfully!"));
                }
            }
        }, 10L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        if (Budget.getInstance().getProfileStorage().findProfile(player) == null) return;
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.QUEUEING) {
            if (Budget.getInstance().getQueueStorage().findQueue(player) != null) {
                Budget.getInstance().getQueueStorage().findQueue(player).remove(player);
            }
        }
        if (profile.getParty() != null) {
            profile.getParty().leave(player);
        }
        Budget.getInstance().getProfileStorage().getProfiles().remove(profile);
        profile.save();
        TaskUtil.runTaskLater(() -> {
            if (Budget.getInstance().getMainConfig().getBoolean("debug")) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    if (!player1.isOp()) return;
                    player1.sendMessage(CC.translate("&7&oDEBUG: " + player.getName() + "'s profile was removed successfully!"));
                }
            }
        }, 10L);
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.FIGHTING && (!Budget.getInstance().getMatchStorage().findMatch(player).getKit().isHunger() || Budget.getInstance().getMatchStorage().findMatch(player).getKit().isBoxing())) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSettingsChanged(SettingsChangedEvent event) {
        if (event.getSettings() == Settings.LOBBY_MUSIC && !event.isToggled()) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(event.getPlayer());
            SoundAPI.stopSong(event.getPlayer());
            profile.getDiscMetadata().stop();
        } else if (event.getSettings() == Settings.LOBBY_MUSIC && event.isToggled()) {
            if (Budget.getInstance().getProfileStorage().findProfile(event.getPlayer()).getState() == ProfileState.LOBBY) {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(event.getPlayer());
                profile.getDiscMetadata().start(true);
            }
        }
    }

    /**
     * Monitors damage events where one entity damages another entity, specifically focusing on Player entities.
     * Updates the last attacker for the victim Player if the damager is also a Player or a Player-controlled Projectile.
     * Credits: Praxi (joeleoli)
     *
     * @param event The EntityDamageByEntityEvent triggered when an entity damages another entity.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = null;


            if (event.getDamager() instanceof Player) {
                attacker = (Player) event.getDamager();
            }

            else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();

                if (projectile.getShooter() instanceof Player) {
                    attacker = (Player) projectile.getShooter();
                }
            }

            if (attacker != null) {
                PlayerUtils.setLastAttacker(victim, attacker);
            }
        }
    }

}
