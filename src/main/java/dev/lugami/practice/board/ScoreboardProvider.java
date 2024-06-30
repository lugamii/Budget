package dev.lugami.practice.board;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.party.Party;
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
            try {
                switch (profile.getState()) {
                    case LOBBY:
                        for (String line : Budget.getInstance().getScoreboardConfig().getStringList("LOBBY")) {
                            line = line.replace("<online>", "" + Bukkit.getOnlinePlayers().size())
                                    .replace("<fighting>", "" + Budget.getInstance().getMatchStorage().getInFights())
                                    .replace("<queueing>", "" + Budget.getInstance().getQueueStorage().getInQueues());
                            lines.add(line);
                        }
                        break;
                    case QUEUEING:
                        for (String line : Budget.getInstance().getScoreboardConfig().getStringList("QUEUEING")) {
                            line = line
                                    .replace("<online>", "" + Bukkit.getOnlinePlayers().size())
                                    .replace("<fighting>", "" + Budget.getInstance().getMatchStorage().getInFights())
                                    .replace("<queueing>", "" + Budget.getInstance().getQueueStorage().getInQueues())
                                    .replace("<type>", Budget.getInstance().getQueueStorage().findQueue(player).isRanked() ? "Ranked" : "Unranked")
                                    .replace("<queue>", Budget.getInstance().getQueueStorage().findQueue(player).getKit().getName())
                                    .replace("<elapsed>", Budget.getInstance().getQueueStorage().findQueue(player).getDuration(player));
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
                        break;
                    case SPECTATING:
                        Match match1 = Budget.getInstance().getMatchStorage().findMatch(player);
                        for (String line : Budget.getInstance().getScoreboardConfig().getStringList("MATCH-SPECTATING")) {
                            line = line.replace("<duration>", match1.getDuration()).replace("<player1>", match1.getTeam1().getLeader().getName()).replace("<player2>", match1.getTeam2().getLeader().getName());
                            lines.add(line);
                        }
                        break;
                    case PARTY:
                        Party party = profile.getParty();
                        for (String line : Budget.getInstance().getScoreboardConfig().getStringList("PARTY")) {
                            line = line.replace("<online>", "" + Bukkit.getOnlinePlayers().size())
                                    .replace("<fighting>", "" + Budget.getInstance().getMatchStorage().getInFights())
                                    .replace("<queueing>", "" + Budget.getInstance().getQueueStorage().getInQueues())
                                    .replace("<leader>", party.getLeader().getName())
                                    .replace("<members>", "" + party.getSize());
                            lines.add(line);
                        }
                        break;
                    default:
                        lines.addAll(Budget.getInstance().getScoreboardConfig().getStringList("UNKNOWN"));
                        break;
                }
            } catch (Exception ex) {
                lines.addAll(Budget.getInstance().getScoreboardConfig().getStringList("UNKNOWN"));
            }
        }
        return lines;
    }

}
