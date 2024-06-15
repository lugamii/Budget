package dev.lugami.practice.storage;

import dev.lugami.practice.profile.Profile;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ProfileStorage {

    private final List<Profile> profiles = new ArrayList<>();

    /**
     * Finds a profile by the player's name.
     *
     * @param name the name of the player
     * @return the Profile object if found, otherwise null
     */
    public Profile findProfile(String name) {
        return profiles.stream().filter(profile -> profile.getPlayer().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Finds a profile by the Player object.
     *
     * @param player the Player object
     * @return the Profile object if found, otherwise null
     */
    public Profile findProfile(Player player) {
        return profiles.stream().filter(profile -> profile.getPlayer() == player).findFirst().orElse(null);
    }

    /**
     * Finds a profile by the player's UUID.
     *
     * @param uuid the UUID of the player
     * @return the Profile object if found, otherwise null
     */
    public Profile findProfile(UUID uuid) {
        return profiles.stream().filter(profile -> profile.getUUID() == uuid).findFirst().orElse(null);
    }
}
