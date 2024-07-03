package dev.lugami.practice.match;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.utils.*;
import dev.lugami.practice.utils.fake.FakePlayer;
import dev.lugami.practice.utils.fake.FakePlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class Match {

    private final UUID matchId;
    private final Team team1;
    private final Team team2;
    private final Kit kit;
    private final Arena arena;
    private final QueueType queueType;
    private MatchState state;
    private Team winnerTeam;
    private Long startedAt;
    private boolean npcTesting = false;
    private List<Player> spectators;

    public enum MatchState {
        WAITING,
        COUNTDOWN,
        IN_PROGRESS,
        ENDED
    }

    public Match(Kit kit, Arena arena) {
        this(kit, arena, QueueType.UNRANKED);
    }

    public Match(Kit kit, Arena arena, boolean npcTesting) {
        this(kit, arena, QueueType.UNRANKED);
        this.npcTesting = npcTesting;
    }

    public Match(Kit kit, Arena arena, QueueType queueType) {
        this.matchId = UUID.randomUUID();
        this.kit = kit;
        this.arena = arena;
        this.queueType = queueType;
        this.state = MatchState.WAITING;
        this.team1 = new Team(null);
        this.team2 = new Team(null);
        this.spectators = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds a player to team 1 if the match is in the WAITING state and equips them with the kit's items.
     *
     * @param player the player to add
     */
    public void addPlayerToTeam1(Player player) {
        if (this.state == MatchState.WAITING) {
            if (this.team1.contains(player) || this.team2.contains(player)) return;
            this.team1.addMember(player);
            if (this.team1.getLeader() == null) this.team1.setLeader(player);
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.FIGHTING);
            profile.setMatchState(MatchPlayerState.ALIVE);
            this.equipPlayer(player);
        } else {
            Budget.getInstance().getLogger().warning("Cannot add players after the match has started.");
        }
    }

    /**
     * Adds a player to team 2 if the match is in the WAITING state and equips them with the kit's items.
     *
     * @param player the player to add
     */
    public void addPlayerToTeam2(Player player) {
        if (this.state == MatchState.WAITING) {
            if (this.team1.contains(player) || this.team2.contains(player)) return;
            this.team2.addMember(player);
            if (this.team2.getLeader() == null) this.team2.setLeader(player);
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.FIGHTING);
            profile.setMatchState(MatchPlayerState.ALIVE);
            this.equipPlayer(player);
        } else {
            Budget.getInstance().getLogger().warning("Cannot add players after the match has started.");
        }
    }

    public void addSpectator(Player player, boolean silent) {
        if (this.state != MatchState.ENDED) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            if (profile.isAtSpawn()) {
                if (!npcTesting) {
                    for (UUID uuid : this.team1.getMembers()) {
                        Player player1 = Bukkit.getPlayer(uuid);
                        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(player1);
                        if (!profile1.getProfileOptions().getSettingsMap().get(Setting.ALLOW_SPECTATORS)) {
                            player.sendMessage(CC.translate("&aA player is not allowing spectators in this match."));
                            break;
                        }
                    }
                    for (UUID uuid : this.team2.getMembers()) {
                        Player player1 = Bukkit.getPlayer(uuid);
                        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(player1);
                        if (!profile1.getProfileOptions().getSettingsMap().get(Setting.ALLOW_SPECTATORS)) {
                            player.sendMessage(CC.translate("&aA player is not allowing spectators in this match."));
                            break;
                        }
                    }
                }
                profile.setState(ProfileState.SPECTATING);
                player.teleport(this.arena.getCuboid().getCenter());
                PlayerUtils.joinSpectator(player);
                this.spectators.add(player);
                Budget.getInstance().getHotbarStorage().resetHotbar(player);
                if (!silent) sendMessage("&b" + player.getName() + " &eis spectating the match.");
            } else {
                player.sendMessage(ChatColor.RED + "You cannot do this right now.");
                return;
            }
        }
    }

    public void removeSpectator(Player player, boolean silent) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (this.spectators.contains(player) && profile.getState() == ProfileState.SPECTATING) {
            Budget.getInstance().getLobbyStorage().bringToLobby(player);
            this.spectators.remove(player);
            if (!silent) sendMessage("&b" + player.getName() + " &cis no longer spectating the match.");
        }
    }

    /**
     * Starts the match with a 5-second countdown, changing the state to COUNTDOWN and then to IN_PROGRESS.
     */
    public void start() {
        Budget.getInstance().getMatchStorage().getMatches().add(this);
        this.sendMessage(ChatColor.GOLD + "A " + (this.queueType == QueueType.RANKED ? "ranked " : "unranked ") + "duel between " + this.team1.getLeader().getName() + (this.team1.getSize() >= 2 ? "'s team" : "") + " and " + this.team2.getLeader().getName() + (this.team2.getSize() >= 2 ? "'s team" : "") + " is starting!");
        if (this.state == MatchState.WAITING) {
            this.state = MatchState.COUNTDOWN;
            if (!npcTesting) {
                teleportTeamsToArena();
            } else {
                teleportNPCSToArena();
            }
            new BukkitRunnable() {
                int countdown = 5;

                @Override
                public void run() {
                    if (countdown == 0) {
                        state = MatchState.IN_PROGRESS;
                        sendMessage(ChatColor.GREEN + "The match has started!");
                        (new MatchStartEvent(Match.this, team1, team2)).call();
                        startedAt = System.currentTimeMillis();
                        this.cancel();
                    } else {
                        sendMessage(ChatColor.YELLOW + "Match starting in " + countdown + " seconds...");
                        countdown--;
                    }
                }
            }.runTaskTimer(Budget.getInstance(), 0L, 20L);
        } else {
            Budget.getInstance().getLogger().warning("Match already started or ended.");
        }
    }

    public void onDeath(Player player) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        Location location = player.getLocation().clone();
        Player killer = PlayerUtils.getLastAttacker(player);
        if (killer != null) {
            Profile killerProfile = Budget.getInstance().getProfileStorage().findProfile(killer);
            if (killerProfile.getProfileOptions().getSettingsMap().get(Setting.LIGHTNING)) {
                team1.doAction(player1 -> LightningUtil.spawnLighting(player1, location));
                team2.doAction(player1 -> LightningUtil.spawnLighting(player1, location));
                spectators.forEach(player1 -> LightningUtil.spawnLighting(player1, location));
            }

            if (killerProfile.getProfileOptions().getSettingsMap().get(Setting.EXPLOSION)) {
                team1.doAction(player1 -> ExplosionUtil.spawnExplosion(player1, location));
                team2.doAction(player1 -> ExplosionUtil.spawnExplosion(player1, location));
                spectators.forEach(player1 -> ExplosionUtil.spawnExplosion(player1, location));
            }
        }

        player.setHealth(20);
        PlayerUtils.respawnPlayer(player);
        player.setFireTicks(0);
        PlayerUtils.hidePlayer(player);
        profile.setMatchState(MatchPlayerState.DEAD);
        MatchSnapshot snap = new MatchSnapshot(player, getOpponent(getTeam(player)).getLeader(), player.getInventory().getArmorContents(), player.getInventory().getContents());
        Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
        player.setVelocity(new Vector());
        player.teleport(location);
        PlayerUtils.resetPlayer(player, false);
        end(getOpponent(getTeam(player)));
    }

    /**
     * Ends the match, sets the winner team, changes the state to ENDED, and announces the winner.
     *
     * @param winningTeam the winning team of the match
     */
    public void end(Team winningTeam) {
        if (this.state == MatchState.IN_PROGRESS) {
            this.state = MatchState.ENDED;
            this.winnerTeam = winningTeam;
            Player winnerLeader = winningTeam.getLeader();
            if (winnerLeader != null) {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(winnerLeader);
                profile.setMatchState(MatchPlayerState.DEAD);
                this.sendMessage(winnerLeader.getName() + (winningTeam.getSize() >= 2 ? "'s team" : "") + " has won the match!");
                (new MatchEndEvent(this, this.winnerTeam, this.getOpponent(winnerTeam))).call();
                MatchSnapshot snap = new MatchSnapshot(winnerLeader, this.getOpponent(winnerTeam).getLeader(), winnerLeader.getInventory().getArmorContents(), winnerLeader.getInventory().getContents());
                Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
            }
            TaskUtil.runTaskLater(() -> {
                if (!npcTesting) this.teleportTeamsToSpawn();
                else {
                    for (Player player : this.spectators) {
                        this.removeSpectator(player, true);
                    }
                }
                Budget.getInstance().getMatchStorage().getMatches().remove(this);
            }, 20 * 3);
        } else {
            Budget.getInstance().getLogger().warning("Match already ended.");
        }
    }



    /**
     * Equips the player with the items from the kit.
     *
     * @param player the player to equip
     */
    private void equipPlayer(Player player) {
        // Equip the player with the kit's items
        player.getInventory().clear();
        player.getInventory().setContents(this.kit.getInventory());
        player.getInventory().setArmorContents(this.kit.getArmor());
    }

    private void teleportTeamsToArena() {
        TaskUtil.runTaskLater(() -> {
            for (UUID playerId : this.team1.getMembers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    Location location = new Location(arena.getPos1().getWorld(), arena.getPos1().getX(), arena.getPos1().getY(), arena.getPos1().getZ(), arena.getPos1().getYaw(), arena.getPos1().getPitch());
                    location.add(0.0, 1.0, 0.0);
                    player.teleport(location);
                }
            }
            for (UUID playerId : this.team2.getMembers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    Location location = new Location(arena.getPos2().getWorld(), arena.getPos2().getX(), arena.getPos2().getY(), arena.getPos2().getZ(), arena.getPos2().getYaw(), arena.getPos2().getPitch());
                    location.add(0.0, 1.0, 0.0);
                    player.teleport(location);
                }
            }
        }, 1L);
    }

    private void teleportTeamsToSpawn() {
        TaskUtil.runTaskLater(() -> {
            for (UUID playerId : this.team1.getMembers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    Budget.getInstance().getLobbyStorage().bringToLobby(player);
                }
            }
            for (UUID playerId : this.team2.getMembers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    Budget.getInstance().getLobbyStorage().bringToLobby(player);
                }
            }

            for (Player player : this.spectators) {
                this.removeSpectator(player, true);
            }
        }, 1L);
    }

    private void teleportNPCSToArena() {
        TaskUtil.runTaskLater(() -> {
            if (!FakePlayerUtils.getFakePlayers().contains((CraftPlayer) team1.getLeader()) || FakePlayerUtils.getFakePlayers().contains((CraftPlayer) team2.getLeader())) return;
            for (UUID playerId : this.team1.getMembers()) {
                FakePlayer player = FakePlayerUtils.getByUUID(playerId);
                if (player != null) {
                    Location location = new Location(arena.getPos1().getWorld(), arena.getPos1().getX(), arena.getPos1().getY(), arena.getPos1().getZ(), arena.getPos1().getYaw(), arena.getPos1().getPitch());
                    location.add(0.0, 1.0, 0.0);
                    player.teleport(location);
                }
            }
            for (UUID playerId : this.team2.getMembers()) {
                FakePlayer player = FakePlayerUtils.getByUUID(playerId);
                if (player != null) {
                    Location location = new Location(arena.getPos2().getWorld(), arena.getPos2().getX(), arena.getPos2().getY(), arena.getPos2().getZ(), arena.getPos2().getYaw(), arena.getPos2().getPitch());
                    location.add(0.0, 1.0, 0.0);
                    player.teleport(location);
                }
            }
        }, 1L);
    }

    public boolean isPlayerInMatch(Player player) {
        return this.team1.contains(player) || this.team2.contains(player) || this.spectators.contains(player);
    }

    public void sendMessage(String message) {
        team1.sendMessage(message);
        team2.sendMessage(message);
        for (Player player : this.spectators) {
            player.sendMessage(CC.translate(message));
        }
    }

    public Team getTeam(Player player) {
        if (team1.contains(player)) return team1;
        else if (team2.contains(player)) return team2;
        else return null;
    }

    public Team getOpponent(Team team) {
        if (team1 == team) return team2;
        else if (team2 == team) return team1;
        else return null;
    }

    public String getDuration() {
        switch (state) {
            case COUNTDOWN:
                return "Starting";
            case IN_PROGRESS:
                return TimeUtils.formatTime(System.currentTimeMillis() - startedAt);
            case ENDED:
                return "Ended";
            default:
                return "Waiting";
        }
    }
}
