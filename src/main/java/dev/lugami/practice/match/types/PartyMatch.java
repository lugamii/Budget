package dev.lugami.practice.match.types;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.team.FFATeam;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.party.Party;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.TaskUtil;
import dev.lugami.practice.utils.TitleAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class PartyMatch extends DefaultMatch {

    private MatchType type;
    private Party party;
    private FFATeam ffaTeam;

    public PartyMatch(Kit kit, Arena arena, MatchType type, Party party) {
        super(kit, arena);
        this.setType(type);
        this.setParty(party);
        if (type == MatchType.FFA) {
            ffaTeam = new FFATeam(null);
        }
    }

    public enum MatchType {
        SPLIT,
        FFA
    }

    @Override
    public void addPlayerToTeam1(Player player) {
        if (this.type == MatchType.FFA) {
            this.addPlayerToFFA(player);
            return;
        }
        super.addPlayerToTeam1(player);
    }

    @Override
    public void addPlayerToTeam2(Player player) {
        if (this.type == MatchType.FFA) {
            this.addPlayerToFFA(player);
            return;
        }
        super.addPlayerToTeam2(player);
    }

    public void addPlayerToFFA(Player player) {
        if (this.type == MatchType.FFA) {
            if (this.ffaTeam.contains(player)) return;
            this.ffaTeam.addMember(player);
            if (this.ffaTeam.getLeader() == null) this.ffaTeam.setLeader(player);
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.FIGHTING);
            profile.setMatchState(MatchPlayerState.ALIVE);
            this.equipPlayer(player);
        }
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
                        YamlConfiguration config = Budget.getInstance().getLanguageConfig();
                        doAction(player -> {
                            if (config.getBoolean("TITLES.MATCH-STARTED.ENABLED"))
                                TitleAPI.sendTitle(player, config.getString("TITLES.MATCH-STARTED.TITLE"), config.getString("TITLES.MATCH-STARTED.SUBTITLE"), config.getInt("TITLES.MATCH-STARTED.FADE-IN"), config.getInt("TITLES.MATCH-STARTED.STAY"), config.getInt("TITLES.MATCH-STARTED.FADE-OUT"));
                        });
                        sendMessage(ChatColor.GREEN + "The match has started!");
                        (new MatchStartEvent(PartyMatch.this, getTeam1(), getTeam2())).call();
                        setStartedAt(System.currentTimeMillis());
                        this.cancel();
                    } else {
                        YamlConfiguration config = Budget.getInstance().getLanguageConfig();
                        doAction(player -> {
                            if (config.getBoolean("TITLES.MATCH-STARTING.ENABLED")) TitleAPI.sendTitle(player, config.getString("TITLES.MATCH-STARTING.TITLE").replace("<countdown>", "" + countdown), config.getString("TITLES.MATCH-STARTING.SUBTITLE").replace("<countdown>", "" + countdown), config.getInt("TITLES.MATCH-STARTING.FADE-IN"), config.getInt("TITLES.MATCH-STARTING.STAY"), config.getInt("TITLES.MATCH-STARTING.FADE-OUT"));
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

    public void teleportTeamsToArena() {
        if (this.type == MatchType.SPLIT) {
            super.teleportTeamsToArena();
        } else {
            TaskUtil.runTaskLater(() -> {
                Location location = new Location(getArena().getPos1().getWorld(), getArena().getPos1().getX(), getArena().getPos1().getY(), getArena().getPos1().getZ(), getArena().getPos1().getYaw(), getArena().getPos1().getPitch());
                location.add(0.0, 1.0, 0.0);
                this.ffaTeam.doAction(player -> player.teleport(location));
            }, 1L);
        }
    }

    @Override
    public void sendMessage(String message) {
        if (this.type == MatchType.SPLIT) {
            super.sendMessage(message);
        } else {
            this.ffaTeam.sendMessage(message);
        }
    }
}
