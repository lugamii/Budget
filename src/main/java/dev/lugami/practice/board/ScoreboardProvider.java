package dev.lugami.practice.board;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.match.types.PartyMatch;
import dev.lugami.practice.party.Party;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.editor.EditingMetadata;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.utils.PlayerUtils;
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
                    case LOBBY_SPECTATE:
                        for (String line : Budget.getInstance().getScoreboardConfig().getStringList("SPECTATOR_MODE")) {
                            line = line.replace("<online>", "" + Bukkit.getOnlinePlayers().size())
                                    .replace("<fighting>", "" + Budget.getInstance().getMatchStorage().getInFights())
                                    .replace("<queueing>", "" + Budget.getInstance().getQueueStorage().getInQueues());
                            lines.add(line);
                        }
                        break;
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
                                    if (match.isPartyMatch()) {
                                        PartyMatch partyMatch = (PartyMatch) match;
                                        if (partyMatch.getType() == PartyMatch.MatchType.FFA) {
                                            for (String line : Budget.getInstance().getScoreboardConfig().getStringList("MATCH-FFA")) {
                                                line = line.
                                                        replace("<remaining>", "" + (int) partyMatch.getFfaTeam().getMembers().stream().filter(player1 ->
                                                                Budget.getInstance().getProfileStorage().findProfile(player1.getPlayer()).getMatchState() == MatchPlayerState.ALIVE
                                                        ).count())
                                                        .replace("<size>", "" + partyMatch.getFfaTeam().getSize())
                                                        .replace("<duration>", match.getDuration());
                                                lines.add(line);
                                            }
                                        } else {
                                            for (String line : Budget.getInstance().getScoreboardConfig().getStringList("MATCH-ONGOING")) {
                                                line = line.
                                                        replace("<opponent>", match.getOpponent(match.getTeam(player)).getLeader().getName() + (match.getOpponent(match.getTeam(player)).getSize() >= 2 ? "'s team" : "")).
                                                        replace("<duration>", match.getDuration());
                                                lines.add(line);
                                            }
                                        }
                                    } else {
                                        if (!match.getKit().isBoxing()) {
                                            for (String line : Budget.getInstance().getScoreboardConfig().getStringList("MATCH-ONGOING")) {
                                                line = line.
                                                        replace("<opponent>", match.getOpponent(match.getTeam(player)).getLeader().getName() + (match.getOpponent(match.getTeam(player)).getSize() >= 2 ? "'s team" : "")).
                                                        replace("<duration>", match.getDuration()).
                                                        replace("<own_ping>", "" + PlayerUtils.getPing(player)).
                                                        replace("<opponent_ping>", "" + PlayerUtils.getPing(match.getOpponent(match.getTeam(player)).getLeader()));
                                                lines.add(line);
                                            }
                                        } else {
                                            for (String line : Budget.getInstance().getScoreboardConfig().getStringList("MATCH-ONGOING-BOXING")) {
                                                line = line.
                                                        replace("<opponent>", match.getOpponent(match.getTeam(player)).getLeader().getName() + (match.getOpponent(match.getTeam(player)).getSize() >= 2 ? "'s team" : "")).
                                                        replace("<duration>", match.getDuration()).
                                                        replace("<own_ping>", "" + PlayerUtils.getPing(player)).
                                                        replace("<opponent_ping>", "" + PlayerUtils.getPing(match.getOpponent(match.getTeam(player)).getLeader())).
                                                        replace("<own_hits>", "" + match.getTeam(player).getMember(player).getHits()).
                                                        replace("<opponent_hits>", "" + match.getOpponent(match.getTeam(player)).getMember(match.getOpponent(match.getTeam(player)).getLeader()).getHits()).
                                                        replace("<diff>", match.getHitDiff(player, match.getOpponent(match.getTeam(player)).getLeader()));
                                                lines.add(line);
                                            }
                                        }
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
                            line = line.
                                    replace("<duration>", match1.getDuration()).
                                    replace("<player1>", match1.getTeam1().getLeader().getName()).
                                    replace("<player2>", match1.getTeam2().getLeader().getName()).
                                    replace("<player1_ping>", "" + PlayerUtils.getPing(match1.getTeam1().getLeader())).
                                    replace("<player2_ping>", "" + PlayerUtils.getPing(match1.getTeam2().getLeader()));
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
                    case EDITOR:
                        EditingMetadata meta = profile.getEditingMetadata();
                        if (meta == null) {
                            lines.addAll(Budget.getInstance().getScoreboardConfig().getStringList("UNKNOWN"));
                            break;
                        }
                        for (String line : Budget.getInstance().getScoreboardConfig().getStringList("EDITOR")) {
                            line = line.replace("<kit>", meta.getEditing().getName());
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
