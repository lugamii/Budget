package dev.lugami.practice.match;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.hotbar.HotbarItem;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.match.team.Team;
import dev.lugami.practice.match.types.PartyMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.profile.editor.CustomKitLayout;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.utils.*;
import dev.lugami.practice.utils.fake.FakePlayer;
import dev.lugami.practice.utils.fake.FakePlayerUtils;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public abstract class Match {

    private final UUID matchId = UUID.randomUUID();
    private final Team team1;
    private final Team team2;

    private final Kit kit;
    private final Arena arena;
    private final QueueType queueType;
    private MatchState state;
    private Team winnerTeam;
    private Long startedAt;
    private boolean npcTesting = false;
    private List<Player> spectators;
    private List<Player> allPlayers;

    public Match(Kit kit, Arena arena) {
        this(kit, arena, QueueType.UNRANKED);
    }

    public Match(Kit kit, Arena arena, boolean npcTesting) {
        this(kit, arena, QueueType.UNRANKED);
        this.npcTesting = npcTesting;
    }

    public Match(Kit kit, Arena arena, QueueType queueType) {
        this.kit = kit;
        this.arena = arena;
        this.queueType = queueType;
        this.state = MatchState.WAITING;
        this.team1 = new Team(null);
        this.team2 = new Team(null);
        this.spectators = new CopyOnWriteArrayList<>();
        this.allPlayers = new CopyOnWriteArrayList<>();
    }

    public enum MatchState {
        WAITING,
        COUNTDOWN,
        IN_PROGRESS,
        ENDED
    }

    public boolean isPartyMatch() {
        return this instanceof PartyMatch;
    }

    public boolean isPlayerInMatch(Player player) {
        if (!isPartyMatch()) {
            return this.team1.contains(player) || this.team2.contains(player) || this.spectators.contains(player);
        } else {
            PartyMatch partyMatch = (PartyMatch) this;
            if (partyMatch.getType() == PartyMatch.MatchType.FFA) {
                return partyMatch.getFfaTeam().contains(player);
            } else {
                return this.team1.contains(player) || this.team2.contains(player) || this.spectators.contains(player);
            }
        }
    }

    /**
     * Adds a player to team 1 if the match is in the WAITING state and equips them with the kit's items.
     *
     * @param player the player to add
     */
    public void addPlayerToTeam1(Player player) {
        if (this.getState() == MatchState.WAITING) {
            if (this.getTeam1().contains(player) || this.getTeam2().contains(player)) return;
            this.getTeam1().addMember(player);
            if (this.getTeam1().getLeader() == null) this.getTeam1().setLeader(player);
            if (!isNpcTesting()) {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                profile.setState(ProfileState.FIGHTING);
                profile.setMatchState(MatchPlayerState.ALIVE);
            }
            this.equipPlayer(player);
            this.getAllPlayers().add(player);
        } else {
            Budget.getInstance().getLogger().warning("Cannot add players after the match has started.");
        }
    }

    /**
     * Adds a player to team 2 if the match is in the WAITING state and equips them with the kit's items.
     *
     * @param player the player to add
     */
    public void addPlayerToTeam2(Player player) {
        if (this.getState() == MatchState.WAITING) {
            if (this.getTeam1().contains(player) || this.getTeam2().contains(player)) return;
            this.getTeam2().addMember(player);
            if (this.getTeam2().getLeader() == null) this.getTeam2().setLeader(player);
            if (!isNpcTesting()) {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                profile.setState(ProfileState.FIGHTING);
                profile.setMatchState(MatchPlayerState.ALIVE);
            }
            this.equipPlayer(player);
            this.getAllPlayers().add(player);
        } else {
            Budget.getInstance().getLogger().warning("Cannot add players after the match has started.");
        }
    }

    public void addSpectator(Player player, boolean silent) {
        if (this.getState() != MatchState.ENDED) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            if (profile.isAtSpawn()) {
                if (!isNpcTesting()) {
                    for (Player player1 : this.getAllPlayers()) {
                        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(player1);
                        if (profile1.getMatchState() != null && profile.isFighting()) {
                            if (!profile1.getProfileOptions().getSettingsMap().get(Setting.ALLOW_SPECTATORS)) {
                                player.sendMessage(CC.translate("&aA player is not allowing spectators in this match."));
                                break;
                            }
                        }
                    }
                }
                profile.setState(ProfileState.SPECTATING);
                player.teleport(this.getArena().getCuboid().getCenter());
                PlayerUtils.joinSpectator(player);
                this.getSpectators().add(player);
                Budget.getInstance().getHotbarStorage().resetHotbar(player);
                if (!silent) sendMessage("&b" + player.getName() + " &eis spectating the match.");
            } else {
                player.sendMessage(Language.CANNOT_DO_ACTION.format());
                return;
            }
        }
    }

    public void removeSpectator(Player player, boolean silent) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (this.getSpectators().contains(player) && profile.getState() == ProfileState.SPECTATING) {
            Budget.getInstance().getLobbyStorage().bringToLobby(player);
            this.getSpectators().remove(player);
            if (!silent) sendMessage("&b" + player.getName() + " &cis no longer spectating the match.");
        }
    }

    /**
     * Starts the match with a 5-second countdown, changing the state to COUNTDOWN and then to IN_PROGRESS.
     */
    public void start() {
        Budget.getInstance().getMatchStorage().getMatches().add(this);
        this.sendMessage(ChatColor.GOLD + "A " + (this.getQueueType() == QueueType.RANKED ? "ranked " : "unranked ") + "duel between " + this.getTeam1().getLeader().getName() + (this.getTeam1().getSize() >= 2 ? "'s team" : "") + " and " + this.getTeam2().getLeader().getName() + (this.getTeam2().getSize() >= 2 ? "'s team" : "") + " is starting!");
        if (this.getState() == MatchState.WAITING) {
            setState(MatchState.COUNTDOWN);
            if (!isNpcTesting()) {
                teleportTeamsToArena();
            } else {
                teleportNPCSToArena();
            }
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
                        (new MatchStartEvent(Match.this, getTeam1(), getTeam2())).call();
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

    public void onDeath(Player player) {
        this.onDeath(player, true);
    }

    public void onDeath(Player player, boolean end) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        Location location = player.getLocation().clone();
        Player killer = PlayerUtils.getLastAttacker(player);
        if (killer != null) {
            Profile killerProfile = Budget.getInstance().getProfileStorage().findProfile(killer);
            if (killerProfile.getProfileOptions().getSettingsMap().get(Setting.LIGHTNING)) {
                getTeam1().doAction(player1 -> LightningUtil.spawnLighting(player1, location));
                getTeam2().doAction(player1 -> LightningUtil.spawnLighting(player1, location));
                getSpectators().forEach(player1 -> LightningUtil.spawnLighting(player1, location));
            }

            if (killerProfile.getProfileOptions().getSettingsMap().get(Setting.EXPLOSION)) {
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
        MatchSnapshot snap = new MatchSnapshot(player, getOpponent(getTeam(player)).getLeader(), player.getInventory().getArmorContents(), player.getInventory().getContents());
        Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
        player.setVelocity(new Vector());
        player.teleport(location);
        PlayerUtils.resetPlayer(player, false);
        if (end) end(getOpponent(getTeam(player)));
    }

    /**
     * Ends the match, sets the winner team, changes the state to ENDED, and announces the winner.
     *
     * @param winningTeam the winning team of the match
     */
    public void end(Team winningTeam) {
        if (this.getState() == MatchState.IN_PROGRESS || this.getState() == MatchState.COUNTDOWN || this.getState() == MatchState.WAITING) {
            this.setState(MatchState.ENDED);
            this.setWinnerTeam(winningTeam);
            this.getWinnerTeam().doAction(player -> {
                YamlConfiguration config = Budget.getInstance().getLanguageConfig();
                if (config.getBoolean("TITLES.MATCH-WINNER.ENABLED"))
                    TitleAPI.sendTitle(player, config.getString("TITLES.MATCH-WINNER.TITLE"), config.getString("TITLES.MATCH-WINNER.SUBTITLE"), config.getInt("TITLES.MATCH-WINNER.FADE-IN"), config.getInt("TITLES.MATCH-WINNER.STAY"), config.getInt("TITLES.MATCH-WINNER.FADE-OUT"));
            });
            this.getOpponent(getWinnerTeam()).doAction(player -> {
                YamlConfiguration config = Budget.getInstance().getLanguageConfig();
                if (config.getBoolean("TITLES.MATCH-LOSER.ENABLED"))
                    TitleAPI.sendTitle(player, config.getString("TITLES.MATCH-LOSER.TITLE"), config.getString("TITLES.MATCH-LOSER.SUBTITLE"), config.getInt("TITLES.MATCH-LOSER.FADE-IN"), config.getInt("TITLES.MATCH-LOSER.STAY"), config.getInt("TITLES.MATCH-LOSER.FADE-OUT"));
            });
            Player winnerLeader = winningTeam.getLeader();
            if (winnerLeader != null) {
                if (!isNpcTesting()) {
                    Profile profile = Budget.getInstance().getProfileStorage().findProfile(winnerLeader);
                    profile.setMatchState(MatchPlayerState.DEAD);
                }
                (new MatchEndEvent(this, this.getWinnerTeam(), this.getOpponent(getWinnerTeam()))).call();
                MatchSnapshot snap = new MatchSnapshot(winnerLeader, this.getOpponent(getWinnerTeam()).getLeader(), winnerLeader.getInventory().getArmorContents(), winnerLeader.getInventory().getContents());
                Budget.getInstance().getMatchStorage().getSnapshots().add(snap);
            }
            TaskUtil.runTaskLater(() -> {
                if (!isNpcTesting()) this.teleportTeamsToSpawn();
                else {
                    for (Player player : this.getSpectators()) {
                        this.removeSpectator(player, true);
                    }
                }
                Budget.getInstance().getMatchStorage().getMatches().remove(this);
            }, 20 * 5);
        } else {
            Budget.getInstance().getLogger().warning("Match already ended.");
        }
    }


    /**
     * Equips the player with the items from the kit.
     *
     * @param player the player to equip
     */
    public void equipPlayer(Player player) {
        // Equip the player with the kit's items
        if (isNpcTesting()) {
            InventoryWrapper wrapper = new InventoryWrapper(player.getInventory());
            wrapper.clear();
            wrapper.setContents(this.getKit().getInventory());
            wrapper.setArmorContents(this.getKit().getArmor());
            return;
        }

        InventoryWrapper wrapper = new InventoryWrapper(player.getInventory());
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        wrapper.clear();
        if (profile.getKitLayouts().get(this.kit) != null && profile.getKitLayouts().get(this.kit).length == 0) {
            wrapper.setContents(this.getKit().getInventory());
            wrapper.setArmorContents(this.getKit().getArmor());
        } else if (profile.getKitLayouts().get(this.kit) != null) {
            if (profile.getKitItems(this.kit).isEmpty() || profile.getKitItems(this.kit).size() == 1) {
                wrapper.setContents(this.getKit().getInventory());
                wrapper.setArmorContents(this.getKit().getArmor());
                return;
            }
            List<HotbarItem> kitItems = profile.getKitItems(this.kit);
            for (int i = 0; i < kitItems.size(); i++) {
                wrapper.setItem(i, kitItems.get(i).getItemStack());
            }
        }
    }

    public void teleportTeamsToArena() {
        TaskUtil.runTaskLater(() -> {
            for (Player player : this.getAllPlayers()) {
                Location location = player.getLocation().clone();
                if (this.getTeam1().getMembers().contains(player.getUniqueId())) {
                    location = new Location(getArena().getPos1().getWorld(), getArena().getPos1().getX(), getArena().getPos1().getY(), getArena().getPos1().getZ(), getArena().getPos1().getYaw(), getArena().getPos1().getPitch());
                } else if (this.getTeam2().getMembers().contains(player.getUniqueId())) {
                    location = new Location(getArena().getPos2().getWorld(), getArena().getPos2().getX(), getArena().getPos2().getY(), getArena().getPos2().getZ(), getArena().getPos2().getYaw(), getArena().getPos2().getPitch());
                }
                location.add(0.0, 1.0, 0.0);
                player.teleport(location);
            }
        }, 1L);
    }

    private void teleportTeamsToSpawn() {
        TaskUtil.runTaskLater(() -> {
            this.getAllPlayers().forEach(Budget.getInstance().getLobbyStorage()::bringToLobby);

            for (Player player : this.getSpectators()) {
                this.removeSpectator(player, true);
            }
        }, 1L);
    }

    private void teleportNPCSToArena() {
        TaskUtil.runTaskLater(() -> {
            if (!FakePlayerUtils.getFakePlayers().contains((FakePlayer) getTeam1().getLeader()) || FakePlayerUtils.getFakePlayers().contains((FakePlayer) getTeam2().getLeader()))
                return;
            for (UUID playerId : this.getTeam1().getMembers()) {
                FakePlayer player = FakePlayerUtils.getByUUID(playerId);
                if (player != null) {
                    Location location = new Location(getArena().getPos1().getWorld(), getArena().getPos1().getX(), getArena().getPos1().getY(), getArena().getPos1().getZ(), getArena().getPos1().getYaw(), getArena().getPos1().getPitch());
                    location.add(0.0, 1.0, 0.0);
                    player.teleport(location);
                }
            }
            for (UUID playerId : this.getTeam2().getMembers()) {
                FakePlayer player = FakePlayerUtils.getByUUID(playerId);
                if (player != null) {
                    Location location = new Location(getArena().getPos2().getWorld(), getArena().getPos2().getX(), getArena().getPos2().getY(), getArena().getPos2().getZ(), getArena().getPos2().getYaw(), getArena().getPos2().getPitch());
                    location.add(0.0, 1.0, 0.0);
                    player.teleport(location);
                }
            }
        }, 1L);
    }

    public void sendMessage(String message) {
        getTeam1().sendMessage(message);
        getTeam2().sendMessage(message);
        for (Player player : this.getSpectators()) {
            player.sendMessage(CC.translate(message));
        }
    }

    public void doAction(Action action) {
        getTeam1().doAction(action);
        getTeam2().doAction(action);
        for (Player player : this.getSpectators()) {
            action.execute(player);
        }
    }

    public Team getTeam(Player player) {
        if (isPartyMatch()) {
            if (((PartyMatch) this).getType() == PartyMatch.MatchType.FFA) {
                return ((PartyMatch) this).getFfaTeam();
            }
        }
        if (getTeam1().contains(player)) return getTeam1();
        else if (getTeam2().contains(player)) return getTeam2();
        else return null;
    }

    public Team getOpponent(Team team) {
        if (isPartyMatch()) {
            if (((PartyMatch) this).getType() == PartyMatch.MatchType.FFA) {
                return team;
            }
        }
        if (getTeam1() == team) return getTeam2();
        else if (getTeam2() == team) return getTeam1();
        else return null;
    }

    public int getAlive() {
        int i = 0;
        if (!this.isPartyMatch() || (this.isPartyMatch() && ((PartyMatch) this).getType() == PartyMatch.MatchType.SPLIT)) {
            i += this.getTeam1().getAlive();
            i += this.getTeam2().getAlive();
        } else {
            PartyMatch match = (PartyMatch) this;
            i += match.getFfaTeam().getAlive();
        }
        return i + 1;
    }

    public String getDuration() {
        try {
            switch (getState()) {
                case COUNTDOWN:
                    return "Starting";
                case IN_PROGRESS:
                    return TimeUtils.formatTime(System.currentTimeMillis() - getStartedAt());
                case ENDED:
                    return "Ended";
                default:
                    return "Waiting";
            }
        } catch (Exception ex) {
            return "Waiting";
        }
    }

}
