package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        if (Budget.getInstance().getProfileStorage().findProfile(player) != null) return;
        Profile profile = new Profile(player);
        Budget.getInstance().getLobbyStorage().bringToLobby(player);
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
        Budget.getInstance().getProfileStorage().getProfiles().remove(Budget.getInstance().getProfileStorage().findProfile(player));
        TaskUtil.runTaskLater(() -> {
            if (Budget.getInstance().getMainConfig().getBoolean("debug")) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    if (!player1.isOp()) return;
                    player1.sendMessage(CC.translate("&7&oDEBUG: " + player.getName() + "'s profile was removed successfully!"));
                }
            }
        }, 10L);
    }

}
