package dev.lugami.practice.storage;

import dev.lugami.practice.profile.Profile;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class ProfileStorage {

    private final List<Profile> profiles = new ArrayList<>();

    public Profile findProfile(String name) {
        return profiles.stream().filter(profile -> profile.getPlayer().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Profile findProfile(Player player) {
        return profiles.stream().filter(profile -> profile.getPlayer() == player).findFirst().orElse(null);
    }

    public Profile findProfile(UUID uuid) {
        return profiles.stream().filter(profile -> profile.getUUID() == uuid).findFirst().orElse(null);
    }

}
