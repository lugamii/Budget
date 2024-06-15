package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.LocationUtil;
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
        if (lobbyLocation == null) {
            lobbyLocation = Bukkit.getWorld("world").getSpawnLocation();
            Budget.getInstance().getMainConfig().set("spawnLocation", LocationUtil.locationToString(lobbyLocation));
            ConfigUtil.saveConfig(Budget.getInstance().getMainConfig(), "config");
        }
    }

    public void bringToLobby(Player player) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile == null) {
            profile = new Profile(player);
        }
        profile.setState(ProfileState.LOBBY);
        player.teleport(lobbyLocation);
    }

}
