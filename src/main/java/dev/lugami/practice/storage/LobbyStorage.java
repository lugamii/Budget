package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.utils.*;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
public class LobbyStorage {

    private Location lobbyLocation;

    /**
     * Constructor that initializes the lobby location from the configuration.
     */
    public LobbyStorage() {
        this.lobbyLocation = LocationUtil.stringToLocation(Budget.getInstance().getMainConfig().getString("spawnLocation"));
    }

    /**
     * Brings a player to the lobby, resetting their state and inventory, and teleporting them to the lobby location.
     *
     * @param player The player to bring to the lobby.
     */
    public void bringToLobby(Player player) {
        this.bringToLobby(player, false);
    }

    /**
     * Brings a player to the lobby, resetting their state and inventory, and teleporting them to the lobby location.
     *
     * @param player The player to bring to the lobby.
     * @param spectate If we should bring the player to Spectator Mode.
     */
    public void bringToLobby(Player player, boolean spectate) {
        TaskUtil.runTask(() -> {
            PlayerUtils.resetPlayer(player, true);
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(spectate ? ProfileState.LOBBY_SPECTATE : ProfileState.LOBBY);
            profile.setMatchState(MatchPlayerState.NONE);
            if (!spectate) {
                if (profile.getEditingMetadata() != null) profile.setEditingMetadata(null);
                if (profile.getParty() != null && profile.getParty().isDisbanded() || profile.getParty() != null && !profile.getParty().contains(player)) {
                    profile.setParty(null);
                } else if (profile.getParty() != null && profile.getParty().contains(player)) {
                    Budget.getInstance().getPartyStorage().bringToParty(player, profile.getParty());
                    player.teleport(this.lobbyLocation);
                    return;
                }
            }

            profile.getDiscMetadata().start(true);

            Budget.getInstance().getHotbarStorage().resetHotbar(player);
            player.teleport(this.lobbyLocation);
        });
    }
}
