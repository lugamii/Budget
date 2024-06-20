package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.match.Team;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.Clickable;
import dev.lugami.practice.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            MatchSnapshot snap = new MatchSnapshot(player, player.getInventory().getArmorContents(), player.getInventory().getContents());
            Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
            PlayerUtils.respawnPlayer(player);
            PlayerUtils.resetPlayer(player);
            Team team = match.getTeam(player);
            match.end(match.getOpponent(team));
            event.getDrops().clear();
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
        Clickable inventories = getClickable(event);
        event.getWinner().sendMessage("");
        event.getWinner().sendMessage(CC.translate("&eWinner: " + event.getWinner().getLeader().getName() + (event.getWinner().getSize() >= 2 ? "'s team" : "")));
        event.getWinner().doAction(inventories::sendToPlayer);
        event.getWinner().sendMessage("");

        event.getLoser().sendMessage("");
        event.getLoser().sendMessage(CC.translate("&eWinner: " + event.getWinner().getLeader().getName() + (event.getWinner().getSize() >= 2 ? "'s team" : "")));
        event.getLoser().doAction(inventories::sendToPlayer);
        event.getLoser().sendMessage("");
    }

    private static Clickable getClickable(MatchEndEvent event) {
        Clickable inventories = new Clickable("&bInventories: ");
        inventories.add("&a" + event.getWinner().getLeader().getName(), "&eClick to view " + event.getWinner().getLeader().getName() + "'s inventory!", "/match inventory " + event.getWinner().getLeader().getName());
        inventories.add("&7, ");
        inventories.add("&c" + event.getLoser().getLeader().getName(), "&eClick to view " + event.getLoser().getLeader().getName() + "'s inventory!", "/match inventory " + event.getLoser().getLeader().getName());
        return inventories;
    }

}
