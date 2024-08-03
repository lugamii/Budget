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

    private final List<TeamPlayer> members;
    private Player leader;

    public Team(Player leader) {
        this.leader = leader;
        this.members = new ArrayList<>();
        if (leader != null) this.members.add(new TeamPlayer(leader));
    }

    public void addMember(Player player) {
        members.add(new TeamPlayer(player));
    }

    public boolean removeMember(Player player) {
        return members.removeIf(p -> p.getPlayer() == player);
    }

    public TeamPlayer getMember(Player player) {
        return members.stream().filter(p -> p.getPlayer() == player).findFirst().orElse(new TeamPlayer(player));
    }

    public void setLeader(Player player) {
        this.leader = player;
        addMember(player);
    }

    public boolean contains(Player player) {
        return members.stream().anyMatch(p -> p.getPlayer() == player);
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
        for (TeamPlayer player : getMembers()) {
            if (!sentPlayers.contains(player.getPlayer())) {
                action.execute(player.getPlayer());
                sentPlayers.add(player.getPlayer());
            }
        }
    }

    public int getAlive() {
        int i = 0;
        for (TeamPlayer player : this.getMembers()) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player.getPlayer());
            if (profile.getMatchState() == MatchPlayerState.ALIVE) {
                i++;
            }
        }
        return i;
    }

}
