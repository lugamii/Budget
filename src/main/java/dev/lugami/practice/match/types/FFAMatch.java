package dev.lugami.practice.match.types;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.match.team.Team;
import dev.lugami.practice.match.team.TeamPlayer;
import dev.lugami.practice.party.Party;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Had to recode party matches, so now Split/FFA are separate types.
 */
@Getter
@Setter
public class FFAMatch extends Match {

    private Team FFATeam;
    private Player winner;
    private Party party;

    public FFAMatch(Kit kit, Arena arena, MatchType type, Party party) {
        super(kit, arena);
        this.setType(type);
        this.setParty(party);
        this.FFATeam = new Team(this.party.getLeader());
    }

    @Override
    public void addPlayerToTeam1(Player player) {
        this.addPlayerToFFA(player);
    }

    @Override
    public void addPlayerToTeam2(Player player) {
        this.addPlayerToFFA(player);
    }

    public void addPlayerToFFA(Player player) {
        if (!this.FFATeam.contains(player) && this.FFATeam.getLeader() != player) {
            this.FFATeam.addMember(player);
        }

        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        profile.setState(ProfileState.FIGHTING);
        profile.setMatchState(MatchPlayerState.ALIVE);
        this.equipPlayer(player);
        this.getAllPlayers().add(player);
    }

    @Override
    public void start() {
        Budget.getInstance().getMatchStorage().getMatches().add(this);
        this.sendMessage(ChatColor.GOLD + "A FFA match between " + this.party.getLeader().getName() + "'s party is starting!");
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
                        (new MatchStartEvent(FFAMatch.this, getTeam1(), getTeam2())).call();
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

    @Override
    public void onDeath(Player player, boolean end) {
        MatchSnapshot snap = new MatchSnapshot(player, getOpponent(getTeam(player)).getLeader(), player.getInventory().getArmorContents(), player.getInventory().getContents());
        Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        Location location = player.getLocation().clone();
        Player killer = PlayerUtils.getLastAttacker(player);
        if (killer != null) {
            Profile killerProfile = Budget.getInstance().getProfileStorage().findProfile(killer);
            if (killerProfile.getProfileOptions().getSettingsMap().get(Settings.LIGHTNING)) {
                getTeam1().doAction(player1 -> LightningUtil.spawnLightning(player1, location));
                getTeam2().doAction(player1 -> LightningUtil.spawnLightning(player1, location));
                getSpectators().forEach(player1 -> LightningUtil.spawnLightning(player1, location));
            }

            if (killerProfile.getProfileOptions().getSettingsMap().get(Settings.EXPLOSION)) {
                getTeam1().doAction(player1 -> ExplosionUtil.spawnExplosion(player1, location));
                getTeam2().doAction(player1 -> ExplosionUtil.spawnExplosion(player1, location));
                getSpectators().forEach(player1 -> ExplosionUtil.spawnExplosion(player1, location));
            }
        }

        player.setHealth(20);
        PlayerUtils.respawnPlayer(player);
        player.setFireTicks(0);
        PlayerUtils.hidePlayer(player);
        profile.setMatchState(MatchPlayerState.DEAD);
        player.setVelocity(new Vector());
        player.teleport(location);
        PlayerUtils.resetPlayer(player, false);
        if (end) {
            this.end(killer);
        }
    }

    public void end(Player player) {
        if (this.getType() == MatchType.FFA) {
            if (this.getState() == MatchState.IN_PROGRESS || this.getState() == MatchState.COUNTDOWN || this.getState() == MatchState.WAITING) {
                this.setState(MatchState.ENDED);
                this.setWinner(player);

                YamlConfiguration config = Budget.getInstance().getLanguageConfig();
                if (config.getBoolean("TITLES.MATCH-WINNER.ENABLED"))
                    TitleAPI.sendTitle(player, config.getString("TITLES.MATCH-WINNER.TITLE"), config.getString("TITLES.MATCH-WINNER.SUBTITLE"), config.getInt("TITLES.MATCH-WINNER.FADE-IN"), config.getInt("TITLES.MATCH-WINNER.STAY"), config.getInt("TITLES.MATCH-WINNER.FADE-OUT"));

                this.doAction(player1 -> {
                    if (player1 != player) {
                        if (config.getBoolean("TITLES.MATCH-LOSER.ENABLED"))
                            TitleAPI.sendTitle(player1, config.getString("TITLES.MATCH-LOSER.TITLE"), config.getString("TITLES.MATCH-LOSER.SUBTITLE"), config.getInt("TITLES.MATCH-LOSER.FADE-IN"), config.getInt("TITLES.MATCH-LOSER.STAY"), config.getInt("TITLES.MATCH-LOSER.FADE-OUT"));

                    }
                });

                if (this.winner != null) {
                    if (!isNpcTesting()) {
                        Profile profile = Budget.getInstance().getProfileStorage().findProfile(winner);
                        profile.setMatchState(MatchPlayerState.DEAD);
                    }
                    MatchSnapshot snap = new MatchSnapshot(winner, this.getLastHit(winner), winner.getInventory().getArmorContents(), winner.getInventory().getContents());
                    Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
                    (new MatchEndEvent(this, getFFATeam(), getFFATeam(), winner, getOpponents(winner))).call();
                }

                TaskUtil.runTaskLater(() -> {
                    if (!isNpcTesting()) this.teleportTeamsToSpawn();
                    else {
                        for (Player player1 : this.getSpectators()) {
                            this.removeSpectator(player1, true);
                        }
                    }
                    Budget.getInstance().getMatchStorage().getMatches().remove(this);
                }, 20 * 5);
            } else {
                Budget.getInstance().getLogger().warning("Match already ended.");
            }
        }
    }

    public List<Player> getOpponents(Player player) {
        List<Player> players = new ArrayList<>();
        doAction(player1 -> {
            if (player1 != player) {
                players.add(player1);
            }
        });
        return players;
    }

    public Player getLastHit(Player player) {
        AtomicReference<Player> lastHitPlayer = new AtomicReference<>();
        Bukkit.getOnlinePlayers().forEach(player1 -> {
            if (PlayerUtils.getLastAttacker(player1) == player) {
                lastHitPlayer.set(player1);
            }
        });
        return lastHitPlayer.get();
    }

    @Override
    public void end(Team winningTeam) {
        super.end(winningTeam);
    }

    @Override
    public void teleportTeamsToArena() {
        TaskUtil.runTaskLater(() -> {
            for (TeamPlayer player : this.getFFATeam().getMembers()) {
                Location location  = new Location(getArena().getPos1().getWorld(), getArena().getPos1().getX(), getArena().getPos1().getY(), getArena().getPos1().getZ(), getArena().getPos1().getYaw(), getArena().getPos1().getPitch());
                location.add(0.0, 1.0, 0.0);
                player.getPlayer().teleport(location);
            }
        }, 1L);
    }

    @Override
    protected void teleportTeamsToSpawn() {
        super.teleportTeamsToSpawn();
    }

    @Override
    protected void teleportNPCSToArena() {
        super.teleportNPCSToArena();
    }

    @Override
    public void doAction(Action action) {
        this.getFFATeam().doAction(action);
    }

    @Override
    public Team getTeam(Player player) {
        return this.getFFATeam();
    }

    @Override
    public Team getOpponent(Team team) {
        return this.getFFATeam();
    }
}
