package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.match.team.Team;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.types.PartyMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.utils.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;

public class MatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            if (PlayerUtils.getLastAttacker(player).getName() != null)
                match.sendMessage("&a" + player.getName() + " &7was killed by &c" + PlayerUtils.getLastAttacker(player).getName() + ".");
            else match.sendMessage("&a" + player.getName() + " &7died.");
            if (match.isPartyMatch()) {
                profile.setMatchState(MatchPlayerState.DEAD);
                if (match.getAlive() >= 1) {
                    match.onDeath(player, false);
                    match.addSpectator(player, true);
                } else {
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
            Match match = Budget.getInstance().getMatchStorage().findMatch(player);
            match.sendMessage("&a" + player.getName() + " &7disconnected.");
            if (match.isPartyMatch()) {
                profile.setMatchState(MatchPlayerState.DEAD);
                match.onDeath(player, match.getAlive() < 1);
            } else {
                match.onDeath(player);
            }
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
            Match match = Budget.getInstance().getMatchStorage().findMatch(event.getPlayer());
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
                if (match.isPartyMatch()) {
                    PartyMatch partyMatch = (PartyMatch) match;
                    if (partyMatch.getType() == PartyMatch.MatchType.SPLIT) {
                        event.setCancelled(partyMatch.getTeam(player) == partyMatch.getTeam(damager));
                    }
                }
                if (!event.isCancelled()) {
                    if (player.isBlocking() && match.getTeam(player).getMember(player).getBlocked() <= 20) {
                        match.getTeam(player).getMember(player).block();
                    } else {
                        ((CraftPlayer) player).getHandle().bU();
                    }
                    match.getTeam(damager).getMember(damager).hit();
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
                if (match.getState() != Match.MatchState.IN_PROGRESS) event.setCancelled(true);
                if (event.getEntity() instanceof ThrownPotion) match.getTeam(shooter).getMember(shooter).pot();
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
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        String eloMessage = null;

        if (match.getQueueType() == QueueType.RANKED) {
            eloMessage = this.handleRankedMatchEnd(match);
        } else {
            this.handleUnrankedMatchEnd(match);
        }

        Clickable inventories = this.getClickable(event);
        String winnerMessage = CC.translate("&eWinner: " + event.getWinner().getLeader().getName() + (event.getWinner().getSize() >= 2 ? "'s team" : ""));

        this.sendMatchEndMessages(match, event.getWinner(), winnerMessage, inventories, eloMessage);
        this.sendMatchEndMessages(match, event.getLoser(), winnerMessage, inventories, eloMessage);
    }

    private String handleRankedMatchEnd(Match match) {
        if (match.isNpcTesting()) {
            return null;
        }
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

    private void handleUnrankedMatchEnd(Match match) {
        if (match.isNpcTesting()) {
            return;
        }
        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(match.getWinnerTeam().getLeader());
        Profile profile2 = Budget.getInstance().getProfileStorage().findProfile(match.getOpponent(match.getWinnerTeam()).getLeader());
        profile1.getStatistics(match.getKit()).setWon(profile1.getStatistics(match.getKit()).getWon() + 1);
        profile2.getStatistics(match.getKit()).setLost(profile2.getStatistics(match.getKit()).getLost() + 1);
        profile1.save();
        profile2.save();
    }

    private void sendMatchEndMessages(Match match, Team team, String winnerMessage, Clickable inventories, String eloMessage) {
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


    private Clickable getClickable(MatchEndEvent event) {
        Clickable inventories = new Clickable("&bInventories: ");
        inventories.add("&a" + event.getWinner().getLeader().getName(), "&eClick to view " + event.getWinner().getLeader().getName() + "'s inventory!", "/match inventory " + event.getWinner().getLeader().getUniqueId());
        inventories.add("&7, ");
        inventories.add("&c" + event.getLoser().getLeader().getName(), "&eClick to view " + event.getLoser().getLeader().getName() + "'s inventory!", "/match inventory " + event.getLoser().getLeader().getUniqueId());
        return inventories;
    }

}
