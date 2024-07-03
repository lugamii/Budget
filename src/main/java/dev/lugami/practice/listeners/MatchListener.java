package dev.lugami.practice.listeners;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import dev.lugami.practice.Budget;
import dev.lugami.practice.match.types.DefaultMatch;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.match.Team;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.types.PartyMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.utils.*;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            DefaultMatch match = Budget.getInstance().getMatchStorage().findMatch(player);
            if (PlayerUtils.getLastAttacker(player).getName() != null) match.sendMessage("&a" + player.getName() + " &7was killed by &c" + PlayerUtils.getLastAttacker(player).getName() + ".");
            else match.sendMessage("&a" + player.getName() + " &7died.");
            if (match.isPartyMatch()) {
                profile.setMatchState(MatchPlayerState.DEAD);
                if (match.getTeam(player).getAlive() >= 1) {
                    match.onDeath(player, false);
                    match.addSpectator(player, true);
                }
                else {
                    match.onDeath(player, true);
                }
            } else {
                match.onDeath(player);
            }
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
                profile.setEnderpearlCooldown(new Cooldown(16_000));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            DefaultMatch match = Budget.getInstance().getMatchStorage().findMatch(player);
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(event.getPlayer());
        if (profile.getState() == ProfileState.FIGHTING) {
            DefaultMatch match = Budget.getInstance().getMatchStorage().findMatch(event.getPlayer());
            if (match == null) return;
            else {
                if (!match.getArena().getCuboid().contains(event.getPlayer().getLocation())) {
                    if (match.getTeam1() == match.getTeam(event.getPlayer())) {
                        event.getPlayer().teleport(match.getArena().getPos1());
                    } else if (match.getTeam2() == match.getTeam(event.getPlayer())) {
                        event.getPlayer().teleport(match.getArena().getPos2());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInventory(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getInventory() == null) return;
        if (event.getClickedInventory().getType() == InventoryType.CREATIVE || event.getInventory().getType() == InventoryType.CREATIVE) {
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                if (profile.getState() == ProfileState.SPECTATING || !player.isOp()) {
                    event.setCancelled(true);
                    event.setCursor(null);
                    event.setCurrentItem(null);
                }
            }
        } else {
            return;
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.TNTPrimed) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(damager);
        if (profile.getState() == ProfileState.FIGHTING && profile1.getState() == ProfileState.FIGHTING) {
            if (profile1.getMatchState() == MatchPlayerState.DEAD || profile.getMatchState() == MatchPlayerState.DEAD) {
                event.setCancelled(true);
                return;
            }

            DefaultMatch match = Budget.getInstance().getMatchStorage().findMatch(player);
            if (match == Budget.getInstance().getMatchStorage().findMatch(damager)) {
                if (match.isPartyMatch()) {
                    PartyMatch partyMatch = (PartyMatch) match;
                    if (partyMatch.getType() == PartyMatch.MatchType.SPLIT) {
                        event.setCancelled(partyMatch.getTeam(player) == partyMatch.getTeam(damager));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        DefaultMatch match = event.getMatch();
        String eloMessage = null;

        if (match.getQueueType() == QueueType.RANKED) {
            eloMessage = handleRankedMatchEnd(match);
        }

        Clickable inventories = getClickable(event);
        String winnerMessage = CC.translate("&eWinner: " + event.getWinner().getLeader().getName() + (event.getWinner().getSize() >= 2 ? "'s team" : ""));

        sendMatchEndMessages(match, event.getWinner(), winnerMessage, inventories, eloMessage);
        sendMatchEndMessages(match, event.getLoser(), winnerMessage, inventories, eloMessage);
    }

    private String handleRankedMatchEnd(DefaultMatch match) {
        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(match.getWinnerTeam().getLeader());
        Profile profile2 = Budget.getInstance().getProfileStorage().findProfile(match.getOpponent(match.getWinnerTeam()).getLeader());

        int player1ELO = profile1.getStatistics(match.getKit()).getElo();
        int player2ELO = profile2.getStatistics(match.getKit()).getElo();

        int[] eloChanges = EloCalculator.calculateElo(player1ELO, player2ELO, match.getWinnerTeam().getLeader() == profile1.getPlayer());
        profile1.getStatistics(match.getKit()).setElo(eloChanges[0]);
        profile2.getStatistics(match.getKit()).setElo(eloChanges[1]);
        profile1.save();
        profile2.save();

        int p1EloChange = eloChanges[0] - player1ELO;
        int p2EloChange = eloChanges[1] - player2ELO;

        return "&aELO Changes: " + match.getWinnerTeam().getLeader().getName() + " +" + p1EloChange + " &7(" + eloChanges[0] + ") &7┃ &c" + match.getOpponent(match.getWinnerTeam()).getLeader().getName() + " " + p2EloChange + " &7(" + eloChanges[1] + ")";
    }

    private void sendMatchEndMessages(DefaultMatch match, Team team, String winnerMessage, Clickable inventories, String eloMessage) {
        team.sendMessage("");
        team.sendMessage(winnerMessage);
        team.doAction(inventories::sendToPlayer);

        if (eloMessage != null) {
            team.sendMessage(eloMessage);
        }
        if (!match.getSpectators().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            Iterator<Player> iterator = match.getSpectators().iterator();

            while (iterator.hasNext()) {
                Player player = iterator.next();
                builder.append(player.getName());
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
            team.sendMessage(CC.translate("&6Spectators &7(&b" + match.getSpectators().size() + "): " + builder));
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
