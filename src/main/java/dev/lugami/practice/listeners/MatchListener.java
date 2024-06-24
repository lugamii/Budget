package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.match.Team;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation().clone();
        event.setDeathMessage(null);
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            player.setHealth(20);
            PlayerUtils.respawnPlayer(player);
            player.setFireTicks(0);
            PlayerUtils.hidePlayer(player);
            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            Team team = match.getTeam(player);
            MatchSnapshot snap = new MatchSnapshot(player, match.getOpponent(team).getLeader(), player.getInventory().getArmorContents(), player.getInventory().getContents());
            Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
            player.setVelocity(new Vector());
            player.teleport(location);
            PlayerUtils.resetPlayer(player, false);
            match.end(match.getOpponent(team));
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onEnderPearl(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player) || !(event.getEntity() instanceof EnderPearl)) {
            return;
        }
        EnderPearl enderPearl = (EnderPearl) event.getEntity();
        Player player = (Player) enderPearl.getShooter();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            if (!profile.getEnderpearlCooldown().hasExpired()) {
                event.setCancelled(true);
                player.sendMessage(CC.translate("&cYou're still on cooldown. Remaining: " + profile.getEnderpearlCooldown().getTimeLeft()));
                InventoryWrapper wrapper = new InventoryWrapper(player.getInventory());
                wrapper.addItem(new ItemStack(Material.ENDER_PEARL));
            } else {
                profile.setEnderpearlCooldown(new Cooldown(15_000));
            }
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
    public void onItemDrop(PlayerDropItemEvent event) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(event.getPlayer());
        if (profile.getState() == ProfileState.FIGHTING) {
            if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
                event.getItemDrop().remove();
                return;
            }
            if (event.getItemDrop().getItemStack().getType().name().endsWith("_SWORD")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        String eloMessage = null;

        if (match.getQueueType() == QueueType.RANKED) {
            eloMessage = handleRankedMatchEnd(match);
        }

        Clickable inventories = getClickable(event);
        String winnerMessage = CC.translate("&eWinner: " + event.getWinner().getLeader().getName() + (event.getWinner().getSize() >= 2 ? "'s team" : ""));

        sendMatchEndMessages(event.getWinner(), winnerMessage, inventories, eloMessage);
        sendMatchEndMessages(event.getLoser(), winnerMessage, inventories, eloMessage);
    }

    private String handleRankedMatchEnd(Match match) {
        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(match.getWinnerTeam().getLeader());
        Profile profile2 = Budget.getInstance().getProfileStorage().findProfile(match.getOpponent(match.getWinnerTeam()).getLeader());

        int player1ELO = profile1.getStatistics(match.getKit()).getElo();
        int player2ELO = profile2.getStatistics(match.getKit()).getElo();

        int[] eloChanges = EloCalculator.calculateElo(player1ELO, player2ELO, match.getWinnerTeam().getLeader() == profile1.getPlayer());
        profile1.getStatistics(match.getKit()).setElo(eloChanges[0]);
        profile2.getStatistics(match.getKit()).setElo(eloChanges[1]);

        int p1EloChange = eloChanges[0] - player1ELO;
        int p2EloChange = eloChanges[1] - player2ELO;

        return "&aELO Changes: " + match.getWinnerTeam().getLeader().getName() + " +" + p1EloChange + " &7(" + eloChanges[0] + ") &7┃ &c" + match.getOpponent(match.getWinnerTeam()).getLeader().getName() + " " + p2EloChange + " &7(" + eloChanges[1] + ")";
    }

    private void sendMatchEndMessages(Team team, String winnerMessage, Clickable inventories, String eloMessage) {
        team.sendMessage("");
        team.sendMessage(winnerMessage);
        team.doAction(inventories::sendToPlayer);

        if (eloMessage != null) {
            team.sendMessage(eloMessage);
        }

        team.sendMessage("");
    }


    private static Clickable getClickable(MatchEndEvent event) {
        Clickable inventories = new Clickable("&bInventories: ");
        inventories.add("&a" + event.getWinner().getLeader().getName(), "&eClick to view " + event.getWinner().getLeader().getName() + "'s inventory!", "/match inventory " + event.getWinner().getLeader().getName());
        inventories.add("&7, ");
        inventories.add("&c" + event.getLoser().getLeader().getName(), "&eClick to view " + event.getLoser().getLeader().getName() + "'s inventory!", "/match inventory " + event.getLoser().getLeader().getName());
        return inventories;
    }

}
