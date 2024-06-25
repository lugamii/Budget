package dev.lugami.practice.match;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.utils.PlayerUtils;
import dev.lugami.practice.utils.TaskUtil;
import dev.lugami.practice.utils.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Match(Kit kit, Arena arena, QueueType queueType) {
        this.matchId = UUID.randomUUID();
        this.kit = kit;
        this.arena = arena;
        this.queueType = queueType;
        this.state = MatchState.WAITING;
        this.team1 = new Team(null);
        this.team2 = new Team(null);
        this.spectators = new ArrayList<>();
    }

    /**
     * Adds a player to team 1 if the match is in the WAITING state and equips them with the kit's items.
     *
     * @param player the player to add
     */
    public void addPlayerToTeam1(Player player) {
        if (state == MatchState.WAITING) {
            if (team1.contains(player) || team2.contains(player)) return;
            team1.addMember(player);
            if (team1.getLeader() == null) team1.setLeader(player);
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.FIGHTING);
            profile.setMatchState(MatchPlayerState.ALIVE);
            equipPlayer(player);
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
        if (state == MatchState.WAITING) {
            if (team1.contains(player) || team2.contains(player)) return;
            team2.addMember(player);
            if (team2.getLeader() == null) team2.setLeader(player);
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.FIGHTING);
            profile.setMatchState(MatchPlayerState.ALIVE);
            equipPlayer(player);
        } else {
            Budget.getInstance().getLogger().warning("Cannot add players after the match has started.");
        }
    }

    public void addSpectator(Player player, boolean silent) {
        if (state != MatchState.ENDED) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            if (profile.isAtSpawn()) {
                profile.setState(ProfileState.SPECTATING);
                player.teleport(team1.getLeader());
                PlayerUtils.joinSpectator(player);
                this.spectators.add(player);
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
        sendMessage(ChatColor.GOLD + "A " + (queueType == QueueType.RANKED ? "ranked " : "unranked ") + "duel between " + team1.getLeader().getName() + (team1.getSize() >= 2 ? "'s team" : "") + " and " + team2.getLeader().getName() + (team2.getSize() >= 2 ? "'s team" : "") + " is starting!");
        if (state == MatchState.WAITING) {
            state = MatchState.COUNTDOWN;
            teleportTeamsToArena();
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

    /**
     * Ends the match, sets the winner team, changes the state to ENDED, and announces the winner.
     *
     * @param winningTeam the winning team of the match
     */
    public void end(Team winningTeam) {
        if (state == MatchState.IN_PROGRESS) {
            state = MatchState.ENDED;
            this.winnerTeam = winningTeam;
            Player winnerLeader = winningTeam.getLeader();
            if (winnerLeader != null) {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(winnerLeader);
                profile.setMatchState(MatchPlayerState.DEAD);
                sendMessage(winnerLeader.getName() + (winningTeam.getSize() >= 2 ? "'s team" : "") + " has won the match!");
                (new MatchEndEvent(this, winnerTeam, getOpponent(winnerTeam))).call();
                MatchSnapshot snap = new MatchSnapshot(winnerLeader, getOpponent(winnerTeam).getLeader(), winnerLeader.getInventory().getArmorContents(), winnerLeader.getInventory().getContents());
                Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
            }
            TaskUtil.runTaskLater(() -> {
                this.teleportTeamsToSpawn();
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
        player.getInventory().setContents(kit.getInventory());
        player.getInventory().setArmorContents(kit.getArmor());
    }

    private void teleportTeamsToArena() {
        TaskUtil.runTaskLater(() -> {
            for (UUID playerId : team1.getMembers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    Location location = new Location(arena.getPos1().getWorld(), arena.getPos1().getX(), arena.getPos1().getY(), arena.getPos1().getZ(), arena.getPos1().getYaw(), arena.getPos1().getPitch());
                    location.add(0.0, 1.0, 0.0);
                    player.teleport(location);
                }
            }
            for (UUID playerId : team2.getMembers()) {
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
            for (UUID playerId : team1.getMembers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    Budget.getInstance().getLobbyStorage().bringToLobby(player);
                }
            }
            for (UUID playerId : team2.getMembers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    Budget.getInstance().getLobbyStorage().bringToLobby(player);
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
