package dev.lugami.practice.match.types;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.party.Party;
import dev.lugami.practice.utils.TitleAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Had to recode party matches, so now Split/FFA are separate types.
 */
@Getter @Setter
public class SplitMatch extends Match {

    private Party party;

    public SplitMatch(Kit kit, Arena arena, MatchType type, Party party) {
        super(kit, arena);
        this.setType(type);
        this.setParty(party);
    }


    /**
     * Starts the match with a 5-second countdown, changing the state to COUNTDOWN and then to IN_PROGRESS.
     */
    @Override
    public void start() {
        Budget.getInstance().getMatchStorage().getMatches().add(this);
        this.sendMessage(ChatColor.GOLD + "A Split match between " + this.party.getLeader().getName() + "'s party &7(&a" + this.getTeam1().getLeader().getName() + " &7vs. &c" + this.getTeam2().getLeader().getName() + "&7) &6is starting!");
        if (this.getState() == MatchState.WAITING) {
            this.setState(MatchState.COUNTDOWN);
            teleportTeamsToArena();
            new BukkitRunnable() {
                int countdown = 5;

                @Override
                public void run() {
                    if (countdown == 0) {
                        setState(MatchState.IN_PROGRESS);
                        YamlConfiguration config = Budget.getInstance().getLanguageConfig();
                        doAction(player -> {
                            if (config.getBoolean("TITLES.MATCH-STARTED.ENABLED"))
                                TitleAPI.sendTitle(player, config.getString("TITLES.MATCH-STARTED.TITLE"), config.getString("TITLES.MATCH-STARTED.SUBTITLE"), config.getInt("TITLES.MATCH-STARTED.FADE-IN"), config.getInt("TITLES.MATCH-STARTED.STAY"), config.getInt("TITLES.MATCH-STARTED.FADE-OUT"));
                        });
                        sendMessage(ChatColor.GREEN + "The match has started!");
                        (new MatchStartEvent(SplitMatch.this, getTeam1(), getTeam2())).call();
                        setStartedAt(System.currentTimeMillis());
                        this.cancel();
                    } else {
                        YamlConfiguration config = Budget.getInstance().getLanguageConfig();
                        doAction(player -> {
                            if (config.getBoolean("TITLES.MATCH-STARTING.ENABLED"))
                                TitleAPI.sendTitle(player, config.getString("TITLES.MATCH-STARTING.TITLE").replace("<countdown>", "" + countdown), config.getString("TITLES.MATCH-STARTING.SUBTITLE").replace("<countdown>", "" + countdown), config.getInt("TITLES.MATCH-STARTING.FADE-IN"), config.getInt("TITLES.MATCH-STARTING.STAY"), config.getInt("TITLES.MATCH-STARTING.FADE-OUT"));
                        });
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
