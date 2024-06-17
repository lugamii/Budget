package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MatchStorage {

    private final List<Match> matches = new ArrayList<>();

    public Match findMatch(UUID uuid) {
        return matches.stream().filter(
                        match ->
                                match.getMatchId() == uuid ||
                                match.getTeam1().getMembers().contains(uuid) ||
                                match.getTeam2().getMembers().contains(uuid))
                        .findFirst().orElse(null);
    }

    public Match findMatch(Player player) {
        return matches.stream().filter(
                        match ->
                                match.getTeam1().getMembers().contains(player.getUniqueId()) ||
                                match.getTeam2().getMembers().contains(player.getUniqueId()))
                .findFirst().orElse(null);
    }

    public int getInFights() {
        int i = 0;
        for (Profile profile : Budget.getInstance().getProfileStorage().getProfiles()) {
            if (profile.getState() == ProfileState.FIGHTING) {
                i++;
            }
        }
        return i;
    }

}
