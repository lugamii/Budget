package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class MatchStorage {

    private final List<Match> matches = new CopyOnWriteArrayList<>();
    private final List<MatchSnapshot> snapshots = new CopyOnWriteArrayList<>();

    /**
     * Finds a match by the unique identifier (UUID).
     *
     * @param uuid The UUID of the match or player in the match.
     * @return The found match, or null if not found.
     */
    public Match findMatch(UUID uuid) {
        return this.matches.stream().filter(match -> match.getMatchId() == uuid || match.isPlayerInMatch(Bukkit.getPlayer(uuid))).findFirst().orElse(null);
    }

    /**
     * Finds a match by the player currently in it.
     *
     * @param player The player in the match.
     * @return The found match, or null if not found.
     */
    public Match findMatch(Player player) {
        return this.matches.stream().filter(match -> match.isPlayerInMatch(player)).findFirst().orElse(null);
    }

    /**
     * Finds a match snapshot by the unique identifier (UUID).
     *
     * @param uuid The UUID of the snapshot or target player.
     * @return The found match snapshot, or null if not found.
     */
    public MatchSnapshot findMatchSnapshot(UUID uuid) {
        return this.snapshots.stream().filter(snapshot -> snapshot.getId() == uuid || snapshot.getTarget().getUniqueId() == uuid).findFirst().orElse(null);
    }

    /**
     * Finds a match snapshot by the player target.
     *
     * @param player The target player in the snapshot.
     * @return The found match snapshot, or null if not found.
     */
    public MatchSnapshot findMatchSnapshot(Player player) {
        return this.snapshots.stream().filter(snapshot -> snapshot.getTarget() == player).findFirst().orElse(null);
    }

    /**
     * Gets the number of players currently in fights.
     *
     * @return The number of players in fights.
     */
    public int getInFights() {
        int i = 0;
        for (Profile profile : Budget.getInstance().getProfileStorage().getProfiles()) {
            if (profile.getState() == ProfileState.FIGHTING) {
                i++;
            }
        }
        return i;
    }

    /**
     * Gets the number of players currently in fights with a kit.
     *
     * @param kit The kit that should be checked for fights.
     * @return The number of players in fights.
     */
    public int getInFights(Kit kit) {
        return getInFights(kit, QueueType.UNRANKED);
    }

    /**
     * Gets the number of players currently in fights with a kit.
     *
     * @param kit The kit that should be checked for fights.
     * @param queueType The queue type to check.
     * @return The number of players in fights.
     */
    public int getInFights(Kit kit, QueueType queueType) {
        try {
            int i = 0;
            for (Profile profile : Budget.getInstance().getProfileStorage().getProfiles()) {
                if (profile.getState() == ProfileState.FIGHTING) {
                    Match match = findMatch(profile.getPlayer());
                    if (match.getKit() == kit && match.getQueueType() == queueType) {
                        i++;
                    }
                }
            }
            return i;
        } catch (ConcurrentModificationException ex) {
            return matches.size() * 2;
        }
    }
}
