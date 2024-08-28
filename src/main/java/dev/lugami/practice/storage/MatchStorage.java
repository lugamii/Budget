package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.team.Team;
import dev.lugami.practice.match.types.FFAMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.Clickable;
import dev.lugami.practice.utils.EloCalculator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
     * Finds a random match.
     */
    public Match getRandomMatch() {
        return getRunningMatches().isEmpty() ? null : getRunningMatches().get(new Random().nextInt(getRunningMatches().size()));
    }

    /**
     * Returns a list of all running matches (a.k.a. in progress)
     */
    public List<Match> getRunningMatches() {
        return this.matches.stream().filter(match -> match.getState() == Match.MatchState.IN_PROGRESS).collect(Collectors.toList());
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

    public String matchEnd(Match match) {
        if (match.getQueueType() == QueueType.RANKED) {
            return this.handleRankedMatchEnd(match);
        } else {
            if (match.isFFAMatch()) {
                this.handleFFAMatchEnd((FFAMatch) match);
            } else {
                this.handleUnrankedMatchEnd(match);
            }
            return null;
        }
    }

    // TODO: Method comment
    private String handleRankedMatchEnd(Match match) {
        if (match.isNpcTesting()) {
            return null;
        }
        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(match.getWinnerTeam().getLeader());
        Profile profile2 = Budget.getInstance().getProfileStorage().findProfile(match.getOpponent(match.getWinnerTeam()).getLeader());

        if (profile1 == null || profile2 == null) return null;

        int player1ELO = profile1.getStatistics(match.getKit()).getElo();
        int player2ELO = profile2.getStatistics(match.getKit()).getElo();

        int[] eloChanges = EloCalculator.calculateElo(player1ELO, player2ELO, match.getWinnerTeam().getLeader() == profile1.getPlayer());

        profile2.getStatistics(match.getKit()).setElo(eloChanges[1]);
        profile1.save();
        profile2.save();

        int p1EloChange = eloChanges[0] - player1ELO;
        int p2EloChange = eloChanges[1] - player2ELO;

        return "&aELO Changes: " + match.getWinnerTeam().getLeader().getName() + " +" + p1EloChange + " &7(" + eloChanges[0] + ") &7┃ &c" + match.getOpponent(match.getWinnerTeam()).getLeader().getName() + " " + p2EloChange + " &7(" + eloChanges[1] + ")";
    }

    // TODO: Method comment
    private void handleUnrankedMatchEnd(Match match) {
        if (match.isNpcTesting()) {
            return;
        }
        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(match.getWinnerTeam().getLeader());
        Profile profile2 = Budget.getInstance().getProfileStorage().findProfile(match.getOpponent(match.getWinnerTeam()).getLeader());
        if (profile1 != null) {
            profile1.getStatistics(match.getKit()).setWon(profile1.getStatistics(match.getKit()).getWon() + 1);
            profile1.save();
        }

        if (profile2 != null) {
            profile2.getStatistics(match.getKit()).setLost(profile2.getStatistics(match.getKit()).getLost() + 1);
            profile2.save();
        }
    }

    // TODO: Method comment
    private void handleFFAMatchEnd(FFAMatch match) {
        if (match.isNpcTesting()) {
            return;
        }
        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(match.getWinner());
        if (profile1 != null) {
            profile1.getStatistics(match.getKit()).setWon(profile1.getStatistics(match.getKit()).getWon() + 1);
            profile1.save();
        }
        match.getFFATeam().getMembers().forEach(tp -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(tp.getPlayer());
            if (profile != null) {
                profile.getStatistics(match.getKit()).setLost(profile.getStatistics(match.getKit()).getLost() + 1);
                profile.save();
            }
        });
    }

    // TODO: Method comment
    public void sendMatchEndMessages(Match match, Team team, String winnerMessage, Clickable inventories, String eloMessage) {
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
            team.sendMessage(CC.translate("&6Spectators &7(&6" + match.getSpectators().size() + "): " + builder));
        }
        team.sendMessage("");
    }

    // TODO: Method comment
    public Clickable getClickable(MatchEndEvent event) {
        Clickable inventories = new Clickable("&6Inventories: ");
        inventories.add("&a" + event.getWinner().getLeader().getName(), "&eClick to view " + event.getWinner().getLeader().getName() + "'s inventory!", "/match inventory " + event.getWinner().getLeader().getUniqueId());
        inventories.add("&7, ");
        inventories.add("&c" + event.getLoser().getLeader().getName(), "&eClick to view " + event.getLoser().getLeader().getName() + "'s inventory!", "/match inventory " + event.getLoser().getLeader().getUniqueId());
        return inventories;
    }

    // TODO: Method comment
    public void sendMatchEndMessages(Match match, List<Player> players, String winnerMessage, Clickable inventories, String eloMessage) {
        players.forEach(player -> {
            player.sendMessage("");
            player.sendMessage(winnerMessage);
            inventories.sendToPlayer(player);

            if (eloMessage != null) {
                player.sendMessage(eloMessage);
            }
            if (!match.getSpectators().isEmpty()) {
                StringBuilder builder = new StringBuilder();
                Iterator<Player> iterator = match.getSpectators().iterator();

                while (iterator.hasNext()) {
                    Player player1 = iterator.next();
                    builder.append(player1.getName());
                    if (iterator.hasNext()) {
                        builder.append(", ");
                    }
                }
                player.sendMessage(CC.translate("&6Spectators &7(&6" + match.getSpectators().size() + "): " + builder));
            }
            player.sendMessage("");
        });

    }

    // TODO: Method comment
    public Clickable getClickableFFA(MatchEndEvent event) {
        Clickable inventories = new Clickable("&6Inventories: ");
        inventories.add("&a" + event.getWinnerPlayer().getName(), "&eClick to view " + event.getWinnerPlayer().getName() + "'s inventory!", "/match inventory " + event.getWinnerPlayer().getUniqueId());
        inventories.add("&7, ");
        Iterator<Player> iterator = event.getLosers().iterator();

        while (iterator.hasNext()) {
            Player player = iterator.next();
            inventories.add("&c" + player.getName(), "&eClick to view " + player.getName() + "'s inventory!", "/match inventory " + player.getUniqueId());
            if (iterator.hasNext()) {
                inventories.add(", ");
            }
        }
        return inventories;
    }
}
