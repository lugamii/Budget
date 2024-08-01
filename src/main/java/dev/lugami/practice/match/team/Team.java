package dev.lugami.practice.match.team;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.Action;
import dev.lugami.practice.utils.CC;
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
        doAction(player -> player.sendMessage(CC.translate(message)));
    }

    public void doAction(Action action) {
        List<Player> sentPlayers = new ArrayList<>();
        for (UUID uuid : getMembers()) {
            if (Bukkit.getPlayer(uuid) == null) return;
            Player player = Bukkit.getPlayer(uuid);
            if (!sentPlayers.contains(player)) {
                action.execute(player);
                sentPlayers.add(player);
            }
        }
    }

    public int getAlive() {
        int i = 0;
        for (UUID uuid : this.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            if (profile.getMatchState() == MatchPlayerState.ALIVE) {
                i++;
            }
        }
        return i;
    }

}
