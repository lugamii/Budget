package dev.lugami.practice.board;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardProvider implements AssembleAdapter {
    @Override
    public String getTitle(Player player) {
        return "&c&lPractice";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        lines.add(CC.SCORE_BAR);
        if (profile == null) {
            lines.add("&fYour profile was not loaded");

        } else {
            if (profile.getState() == ProfileState.LOBBY) {
                lines.add("&cOnline: &f" + Bukkit.getOnlinePlayers().size());
                lines.add("&cPlaying: &f" + Budget.getInstance().getMatchStorage().getInFights());
            } else if (profile.getState() == ProfileState.FIGHTING) {
                Match match = Budget.getInstance().getMatchStorage().findMatch(player);
                if (match == null) {
                    lines.add("&fTrying to find your match...");
                } else {
                    switch (match.getState()) {
                        case WAITING:
                            lines.add("&fWaiting...");
                            break;
                        case COUNTDOWN:
                            lines.add("&fMatch starting...");
                            lines.add("&fOpponent: &c" + match.getOpponent(match.getTeam(player)).getLeader().getName() + (match.getOpponent(match.getTeam(player)).getSize() >= 2 ? "'s team" : ""));
                            break;
                        case IN_PROGRESS:
                            lines.add("&fDuration: &c" + match.getDuration());
                            lines.add("&fOpponent: &c" + match.getOpponent(match.getTeam(player)).getLeader().getName() + (match.getOpponent(match.getTeam(player)).getSize() >= 2 ? "'s team" : ""));
                            break;
                        case ENDED:
                            if (match.getTeam(player) == match.getWinnerTeam()) {
                                lines.add("&aYou won, GGs!");
                            } else {
                                lines.add("&cYou lost!");
                            }
                            break;
                    }
                }
            }
        }
        lines.add("");
        lines.add("&cwww.angolanos.fun");
        lines.add(CC.SCORE_BAR);
        return lines;
    }

}
