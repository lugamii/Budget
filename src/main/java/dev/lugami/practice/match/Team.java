package dev.lugami.practice.match;

import dev.lugami.practice.utils.CC;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Team {

    private final List<UUID> members;
    private Player leader;

    public Team(Player leader) {
        this.leader = leader;
        this.members = new ArrayList<>();
        if (leader != null) this.members.add(leader.getUniqueId());
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());
    }

    public boolean removeMember(Player player) {
        return members.remove(player.getUniqueId());
    }

    public void setLeader(Player player) {
        this.leader = player;
        addMember(player);
    }

    public boolean contains(Player player) {
        return members.contains(player.getUniqueId());
    }

    public int getSize() {
        return members.size() - 1;
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public void sendMessage(String message) {
        List<Player> sentPlayers = new ArrayList<>();
        for (UUID uuid : getMembers()) {
            if (Bukkit.getPlayer(uuid) == null) return;
            Player player = Bukkit.getPlayer(uuid);
            if (!sentPlayers.contains(player)) {
                player.sendMessage(CC.translate(message));
                sentPlayers.add(player);
            }
        }
    }
}
