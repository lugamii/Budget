package dev.lugami.practice.queue;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.TimeUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class Queue {

    private final UUID id = UUID.randomUUID();
    private final Kit kit;
    private final List<Player> players = new ArrayList<>();
    private final Map<Player, Long> addedOn = new HashMap<>();

    public void add(Player player) {
        if (!this.players.contains(player)) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            this.players.add(player);
            this.addedOn.put(player, System.currentTimeMillis());
            profile.setState(ProfileState.QUEUEING);
            Budget.getInstance().getHotbarStorage().resetHotbar(player);
            player.sendMessage(CC.translate("&aYou have been added to the " + kit.getName() + " queue."));
        } else {
            Bukkit.getLogger().warning("[Budget] Tried adding " + player.getName() + " to the " + kit.getName() + " queue, but he's already there (?)");
        }
    }

    public void remove(Player player) {
        if (this.players.contains(player)) {
            this.players.remove(player);
            this.addedOn.remove(player);
            Budget.getInstance().getHotbarStorage().resetHotbar(player);
            player.sendMessage(CC.translate("&cYou have been removed from the " + kit.getName() + " queue."));
        } else {
            Bukkit.getLogger().warning("[Budget] Tried removing " + player.getName() + " from the " + kit.getName() + " queue, but he's not there (?)");
        }
    }

    public String getDuration(Player player) {
        return TimeUtils.formatTime(System.currentTimeMillis() - this.addedOn.getOrDefault(player, System.currentTimeMillis()));
    }

}
