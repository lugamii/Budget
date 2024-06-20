package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.hotbar.HotbarItem;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Setter
@Getter
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
        TaskUtil.runTaskLater(() -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.LOBBY);
            PlayerUtils.resetPlayer(player);
            Budget.getInstance().getHotbarStorage().resetHotbar(player);
            player.teleport(this.lobbyLocation);
        }, 1L);
    }
}
