package dev.lugami.practice.board;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.settings.Setting;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardProvider implements AssembleAdapter {
    @Override
    public String getTitle(Player player) {
        return Budget.getInstance().getScoreboardConfig().getString("TITLE");
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile == null) {
            lines.add("&fYour profile was not loaded");
        } else {
            if (!profile.getProfileOptions().getSettingsMap().get(Setting.SCOREBOARD)) {
                return new ArrayList<>();
            }
            switch (profile.getState()) {
                case LOBBY:
                    for (String line : Budget.getInstance().getScoreboardConfig().getStringList("LOBBY")) {
                        line = line.replace("<online>", "" + Bukkit.getOnlinePlayers().size()).replace("<fighting>", "" + Budget.getInstance().getMatchStorage().getInFights());
                        lines.add(line);
                    }
                    break;
                case QUEUEING:
                    for (String line : Budget.getInstance().getScoreboardConfig().getStringList("QUEUEING")) {
                        line = line.replace("<online>", "" + Bukkit.getOnlinePlayers().size()).replace("<fighting>", "" + Budget.getInstance().getMatchStorage().getInFights()).replace("<queue>", Budget.getInstance().getQueueStorage().findQueue(player).getKit().getName()).replace("<elapsed>", Budget.getInstance().getQueueStorage().findQueue(player).getDuration(player));
                        lines.add(line);
                    }
                    break;
                case FIGHTING:
                    Match match = Budget.getInstance().getMatchStorage().findMatch(player);
                    if (match == null) {
                        lines.addAll(Budget.getInstance().getScoreboardConfig().getStringList("MATCH-WAITING"));
                        break;
                    } else {
                        switch (match.getState()) {
                            default:
                                lines.addAll(Budget.getInstance().getScoreboardConfig().getStringList("MATCH-WAITING"));
                                break;
                            case COUNTDOWN:
                                for (String line : Budget.getInstance().getScoreboardConfig().getStringList("MATCH-STARTING")) {
                                    line = line.replace("<opponent>", "" + match.getOpponent(match.getTeam(player)).getLeader().getName() + (match.getOpponent(match.getTeam(player)).getSize() >= 2 ? "'s team" : ""));
                                    lines.add(line);
                                }
                                break;
                            case IN_PROGRESS:
                                for (String line : Budget.getInstance().getScoreboardConfig().getStringList("MATCH-ONGOING")) {
                                    line = line.
                                            replace("<opponent>", match.getOpponent(match.getTeam(player)).getLeader().getName() + (match.getOpponent(match.getTeam(player)).getSize() >= 2 ? "'s team" : "")).
                                            replace("<duration>", match.getDuration());
                                    lines.add(line);
                                }
                                break;
                            case ENDED:
                                if (match.getWinnerTeam() == match.getTeam(player)) {
                                    lines.addAll(Budget.getInstance().getScoreboardConfig().getStringList("MATCH-WON"));
                                } else {
                                    lines.addAll(Budget.getInstance().getScoreboardConfig().getStringList("MATCH-LOST"));
                                }
                                break;
                        }
                    }
            }
            /*if (profile.getState() == ProfileState.LOBBY) {
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
                        default:
                            lines.add("&fTrying to find your match...");
                            break;
                    }
                }
            }*/
        }
        return lines;
    }

}
