package dev.lugami.practice.match.types;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.party.Party;
import dev.lugami.practice.queue.QueueType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PartyMatch extends DefaultMatch {

    private MatchType type;
    private Party party;
    private List<Player> players;

    public PartyMatch(Kit kit, Arena arena, Party party) {
        this(kit, arena, MatchType.SPLIT, party);
    }

    public PartyMatch(Kit kit, Arena arena, MatchType type, Party party) {
        super(kit, arena);
        this.setType(type);
        this.setParty(party);
        if (type == MatchType.FFA) {
            players = new ArrayList<>();
        }
    }

    public enum MatchType {
        SPLIT,
        FFA
    }

    @Override
    public void addPlayerToTeam1(Player player) {
        if (this.type == MatchType.FFA) {
            this.players.add(player);
            return;
        }
        super.addPlayerToTeam1(player);
    }

    @Override
    public void addPlayerToTeam2(Player player) {
        if (this.type == MatchType.FFA) {
            this.players.add(player);
            return;
        }
        super.addPlayerToTeam2(player);
    }

    /**
     * Starts the match with a 5-second countdown, changing the state to COUNTDOWN and then to IN_PROGRESS.
     */
    @Override
    public void start() {
        Budget.getInstance().getMatchStorage().getMatches().add(this);
        this.sendMessage(ChatColor.GOLD + "A " + (this.type == MatchType.FFA ? "FFA " : "Split ") + "match between " + this.party.getLeader().getName() + "'s party is starting!");
        if (this.getState() == MatchState.WAITING) {
            this.setState(MatchState.COUNTDOWN);
            teleportTeamsToArena();
            new BukkitRunnable() {
                int countdown = 5;

                @Override
                public void run() {
                    if (countdown == 0) {
                        setState(MatchState.IN_PROGRESS);
                        sendMessage(ChatColor.GREEN + "The match has started!");
                        if (PartyMatch.this.getType() == MatchType.SPLIT) {
                            (new MatchStartEvent(PartyMatch.this, getTeam1(), getTeam2())).call();
                        }
                        setStartedAt(System.currentTimeMillis());
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
}
