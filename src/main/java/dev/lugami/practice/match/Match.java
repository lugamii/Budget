package dev.lugami.practice.match;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Match {

    private final UUID matchId;
    private final Set<UUID> team1 = new HashSet<>();
    private final Set<UUID> team2 = new HashSet<>();
    private final Kit kit;
    private final Arena arena;
    private MatchState state;
    private UUID winner;

    public enum MatchState {
        WAITING,
        COUNTDOWN,
        IN_PROGRESS,
        ENDED
    }

    public Match(Kit kit, Arena arena) {
        this.matchId = UUID.randomUUID();
        this.kit = kit;
        this.arena = arena;
        this.state = MatchState.WAITING;
    }

    /**
     * Adds a player to team 1 if the match is in the WAITING state and equips them with the kit's items.
     *
     * @param player the player to add
     */
    public void addPlayerToTeam1(Player player) {
        if (state == MatchState.WAITING) {
            team1.add(player.getUniqueId());
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.FIGHTING);
            equipPlayer(player);
        } else {
            throw new IllegalStateException("Cannot add players after the match has started.");
        }
    }

    /**
     * Adds a player to team 2 if the match is in the WAITING state and equips them with the kit's items.
     *
     * @param player the player to add
     */
    public void addPlayerToTeam2(Player player) {
        if (state == MatchState.WAITING) {
            team2.add(player.getUniqueId());
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.FIGHTING);
            equipPlayer(player);
        } else {
            throw new IllegalStateException("Cannot add players after the match has started.");
        }
    }

    /**
     * Starts the match with a 5-second countdown, changing the state to COUNTDOWN and then to IN_PROGRESS.
     */
    public void start() {
        if (state == MatchState.WAITING) {
            state = MatchState.COUNTDOWN;
            teleportTeamsToArena();
            new BukkitRunnable() {
                int countdown = 5;

                @Override
                public void run() {
                    if (countdown == 0) {
                        state = MatchState.IN_PROGRESS;
                        Bukkit.broadcastMessage(ChatColor.GREEN + "The match has started!");
                        this.cancel();
                    } else {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "Match starting in " + countdown + " seconds...");
                        countdown--;
                    }
                }
            }.runTaskTimer(Budget.getInstance(), 0L, 20L); // Adjust "YourPluginName" to your plugin's name
        } else {
            throw new IllegalStateException("Match already started or ended.");
        }
    }

    /**
     * Ends the match, sets the winner, changes the state to ENDED, and announces the winner.
     *
     * @param winnerId the UUID of the winning player
     */
    public void end(UUID winnerId) {
        if (state == MatchState.IN_PROGRESS) {
            state = MatchState.ENDED;
            this.winner = winnerId;
            // Announce the winner and handle end match logic here
            Player winnerPlayer = Bukkit.getPlayer(winnerId);
            if (winnerPlayer != null) {
                Bukkit.broadcastMessage(winnerPlayer.getName() + " has won the match!");
            }
            TaskUtil.runTaskLater(this::teleportTeamsToSpawn, 20 * 3);
        } else {
            throw new IllegalStateException("Match is not in progress.");
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
        for (UUID playerId : team1) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.teleport(arena.getPos1());
            }
        }
        for (UUID playerId : team2) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.teleport(arena.getPos2());
            }
        }
    }

    private void teleportTeamsToSpawn() {
        for (UUID playerId : team1) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                Budget.getInstance().getLobbyStorage().bringToLobby(player);
            }
        }
        for (UUID playerId : team2) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                Budget.getInstance().getLobbyStorage().bringToLobby(player);
            }
        }
    }

    public boolean isPlayerInMatch(Player player) {
        return team1.contains(player.getUniqueId()) || team2.contains(player.getUniqueId());
    }
}
