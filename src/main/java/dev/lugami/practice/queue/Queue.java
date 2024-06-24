package dev.lugami.practice.queue;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
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
    private final Map<Player, Integer> eloMap = new HashMap<>();
    private final boolean ranked;

    public void add(Player player, QueueType type) {
        if (type == QueueType.RANKED && !this.ranked) {
            throw new IllegalArgumentException("QueueType is ranked but Queue is not");
        }
        if (type == QueueType.UNRANKED && this.ranked) {
            throw new IllegalArgumentException("QueueType is unranked but Queue is ranked");
        }
        if (!this.players.contains(player)) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            this.players.add(player);
            this.addedOn.put(player, System.currentTimeMillis());
            if (type == QueueType.RANKED) this.eloMap.put(player, profile.getStatistics(this.kit).getElo());
            profile.setState(ProfileState.QUEUEING);
            Budget.getInstance().getHotbarStorage().resetHotbar(player);
            player.sendMessage(CC.translate("&aYou have been added to the " + (type == QueueType.RANKED ? "Ranked " : "Unranked ") + kit.getName() + " queue."));
        } else {
            Bukkit.getLogger().warning("[Budget] Tried adding " + player.getName() + " to the " + kit.getName() + " queue, but he's already there (?)");
        }
    }

    public void remove(Player player) {
        if (this.players.contains(player)) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            this.players.remove(player);
            this.addedOn.remove(player);
            this.eloMap.remove(player);
            profile.setState(ProfileState.LOBBY);
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
