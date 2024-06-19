package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MatchStorage {

    private final List<Match> matches = new ArrayList<>();

    public Match findMatch(UUID uuid) {
        return matches.stream().filter(match -> match.getMatchId() == uuid || match.isPlayerInMatch(Bukkit.getPlayer(uuid))).findFirst().orElse(null);
    }

    public Match findMatch(Player player) {
        return matches.stream().filter(match -> match.isPlayerInMatch(player)).findFirst().orElse(null);
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
