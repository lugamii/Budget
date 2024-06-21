package dev.lugami.practice.profile;

import dev.lugami.practice.Budget;
import dev.lugami.practice.utils.Cooldown;
import lombok.Data;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class Profile {

    private final Player player;
    private final UUID UUID;
    private ProfileState state;
    private Cooldown enderpearlCooldown;

    public Profile(Player p) {
        this.player = p;
        this.UUID = p.getUniqueId();
        this.state = ProfileState.LOBBY;
        this.enderpearlCooldown = new Cooldown(0);
        Budget.getInstance().getProfileStorage().getProfiles().add(this);
    }

    public Profile(UUID u) {
        this.player = Bukkit.getPlayer(u);
        this.UUID = u;
        this.state = ProfileState.LOBBY;
        this.enderpearlCooldown = new Cooldown(0);
        Budget.getInstance().getProfileStorage().getProfiles().add(this);
    }

    public Profile(Player p, UUID u) {
        this.player = p;
        this.UUID = u;
        this.state = ProfileState.LOBBY;
        this.enderpearlCooldown = new Cooldown(0);
        Budget.getInstance().getProfileStorage().getProfiles().add(this);
    }

}
