package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.LocationUtil;
import dev.lugami.practice.utils.PlayerUtils;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Setter
@Getter
public class LobbyStorage {

    private Location lobbyLocation;

    public LobbyStorage() {
        lobbyLocation = LocationUtil.stringToLocation(Budget.getInstance().getMainConfig().getString("spawnLocation"));
    }

    public void bringToLobby(Player player) {
        TaskUtil.runTaskLater(() -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.LOBBY);
            PlayerUtils.resetPlayer(player);
            player.teleport(lobbyLocation);
        }, 1L);
    }

}
