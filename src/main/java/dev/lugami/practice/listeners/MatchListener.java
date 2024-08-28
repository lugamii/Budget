package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.match.team.Team;
import dev.lugami.practice.match.types.FFAMatch;
import dev.lugami.practice.match.types.SplitMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.storage.MatchStorage;
import dev.lugami.practice.utils.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;
import java.util.List;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            if (PlayerUtils.getLastAttacker(player) != null && PlayerUtils.getLastAttacker(player).getName() != null)
                match.sendMessage("&a" + player.getName() + " &7was killed by &c" + PlayerUtils.getLastAttacker(player).getName() + ".");
            else match.sendMessage("&a" + player.getName() + " &7died.");
            if (match.isPartyMatch()) {
                profile.setMatchState(MatchPlayerState.DEAD);
                TaskUtil.runTaskLater(() -> {
                    match.onDeath(player, match.canEnd());
                    if (match.getAlive() > 1) match.addSpectator(player, true);
                }, 1);
            } else {
                match.onDeath(player, true);
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
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Material type = item.getType();
        Player player = event.getPlayer();
        if (type.getId() == 373 && Budget.getInstance().getMainConfig().getBoolean("match.remove-bottle-drop")) {
            TaskUtil.runTaskLaterAsynchronously(() -> {
                player.setItemInHand(new ItemStack(Material.AIR));
                player.updateInventory();
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            match.sendMessage("&a" + player.getName() + " &7disconnected.");
            if (match.isPartyMatch()) {
                profile.setMatchState(MatchPlayerState.DEAD);
                match.onDeath(player, match.canEnd());
            } else {
                match.onDeath(player);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(event.getPlayer());
        if (profile.getState() == ProfileState.FIGHTING) {
            if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE && Budget.getInstance().getMainConfig().getBoolean("match.remove-bottle-drop")) {
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
            Match match = Budget.getInstance().getMatchStorage().findMatch(event.getPlayer());
            if (match == null) return;
            else {
                if (!match.getArena().getCuboid().contains(event.getPlayer().getLocation())) {
                    event.setCancelled(true);
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
        if (event.getEntity() instanceof org.bukkit.entity.TNTPrimed && ExplosionUtil.getSpawned().contains(((CraftTNTPrimed) event.getEntity()).getHandle())) {
            event.blockList().clear();
            ExplosionUtil.getSpawned().remove(((CraftTNTPrimed) event.getEntity()).getHandle());
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

            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            if (match == null) return;
            if (match.getState() != Match.MatchState.IN_PROGRESS) {
                event.setCancelled(true);
                return;
            }
            if (match == Budget.getInstance().getMatchStorage().findMatch(damager)) {
                if (match.isPartyMatch() && match.isSplitMatch()) {
                    SplitMatch partyMatch = (SplitMatch) match;
                    event.setCancelled(partyMatch.getTeam(player) == partyMatch.getTeam(damager));
                }
                if (!event.isCancelled()) {
                    if (player.isBlocking() && match.getTeam(player).getMember(player).getBlocked() <= 20) {
                        match.getTeam(player).getMember(player).block();
                    } else {
                        ((CraftPlayer) player).getHandle().bU();
                    }

                    match.getTeam(damager).getMember(damager).hit();

                    if (match.getKit().isBoxing()) {
                        event.setDamage(0.0);
                    }

                    if (match.getTeam(damager).getMember(damager).getHits() == 100 && match.getKit().isBoxing()) {
                        if (match.isPartyMatch()) {
                            profile.setMatchState(MatchPlayerState.DEAD);
                            match.onDeath(player, match.canEnd());
                        } else {
                            match.onDeath(player);
                        }
                    }

                    boolean crit = damager.getFallDistance() > 0.0F
                            && !damager.isOnGround()
                            && !damager.isInsideVehicle()
                            && !damager.hasPotionEffect(PotionEffectType.BLINDNESS)
                            && damager.getLocation().getBlock().getType() != Material.LADDER
                            && damager.getLocation().getBlock().getType() != Material.VINE;
                    if (crit) {
                        match.getTeam(damager).getMember(damager).crit();
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(shooter);
            if (profile.getState() == ProfileState.FIGHTING) {
                Match match = Budget.getInstance().getMatchStorage().findMatch(shooter);
                if (match == null) return;
                if (match.getState() == Match.MatchState.WAITING || match.getState() == Match.MatchState.COUNTDOWN) event.setCancelled(true);
                if (match.getState() == Match.MatchState.IN_PROGRESS) if (event.getEntity() instanceof ThrownPotion) match.getTeam(shooter).getMember(shooter).pot();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) event.getPotion().getShooter();
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(shooter);
            if (profile.getState() == ProfileState.FIGHTING) {
                Match match = Budget.getInstance().getMatchStorage().findMatch(shooter);
                if (match == null) return;
                if (match.getState() == Match.MatchState.IN_PROGRESS) {
                    if (event.getIntensity(shooter) <= 0.5D) match.getTeam(shooter).getMember(shooter).miss();
                }
            }
        }
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();

        if (Budget.getInstance().getMainConfig().getBoolean("match.enable-cps-alert")) {
            match.getTeam1().sendMessage("", "&cButterfly clicking is &4strictly prohibited&c, and doing so might result in a &4punishment.", "");
            match.getTeam2().sendMessage("", "&cButterfly clicking is &4strictly prohibited&c, and doing so might result in a &4punishment.", "");
        }

        if (match.getKit().isBoxing()) {
            match.getTeam1().doAction(player -> player.addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 1)));
            match.getTeam2().doAction(player -> player.addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 1)));
        }
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        if (event.getMatch().isPartyMatch() && !event.getMatch().isSplitMatch()) return;

        MatchStorage matches = Budget.getInstance().getMatchStorage();

        String eloMessage = matches.matchEnd(match);

        Clickable inventories = matches.getClickable(event);
//        String winnerMessage = CC.translate("&eWinner: " + event.getWinner().getLeader().getName() + (event.getWinner().getSize() >= 2 ? "'s team" : ""));

        matches.sendMatchEndMessages(match, event.getWinner(), inventories, eloMessage);
        matches.sendMatchEndMessages(match, event.getLoser(), inventories, eloMessage);
    }

    @EventHandler
    public void onPartyMatchEnd(MatchEndEvent event) {
        if (!event.getMatch().isPartyMatch() || event.getMatch().isSplitMatch()) return;
        Match match = event.getMatch();
        MatchStorage matches = Budget.getInstance().getMatchStorage();
        String eloMessage = matches.matchEnd(match);
        Clickable inventories = matches.getClickableFFA(event);
        String winnerMessage = CC.translate("&eWinner: " + event.getWinnerPlayer().getName());
        List<Player> players = event.getLosers();
        players.add(event.getWinnerPlayer());
        matches.sendMatchEndMessages(match, players, winnerMessage, inventories, eloMessage);
    }

}
