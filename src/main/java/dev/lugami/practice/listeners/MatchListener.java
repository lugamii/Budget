package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.Team;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();
        PlayerUtils.respawnPlayer(player);
        PlayerUtils.resetPlayer(player);
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            Team team = match.getTeam(player);
            match.end(match.getOpponent(team));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            Team team = match.getTeam(player);
            match.end(match.getOpponent(team));
        }
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        event.getWinner().getMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.sendMessage("");
            player.sendMessage(CC.translate("&eWinner: " + event.getWinner().getLeader().getName() + (event.getWinner().getSize() >= 2 ? "'s team" : "")));
            player.sendMessage(CC.translate("&bInventories: &a" + event.getWinner().getLeader().getName() + "&7, " + event.getLoser().getLeader().getName()));
            player.sendMessage("");
        });

        event.getLoser().getMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.sendMessage("");
            player.sendMessage(CC.translate("&eWinner: " + event.getWinner().getLeader().getName() + (event.getWinner().getSize() >= 2 ? "'s team" : "")));
            player.sendMessage(CC.translate("&bInventories: &a" + event.getWinner().getLeader().getName() + "&7, " + event.getLoser().getLeader().getName()));
            player.sendMessage("");
        });
    }

}
