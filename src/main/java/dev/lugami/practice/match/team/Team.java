package dev.lugami.practice.match.team;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.Action;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.DeduplicatingArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter @Setter
public class Team {

    private final DeduplicatingArrayList<TeamPlayer> members;
    private Player leader;

    public Team(Player leader) {
        this.leader = leader;
        this.members = new DeduplicatingArrayList<>();
        if (leader != null) this.members.add(new TeamPlayer(leader));
    }

    public void addMember(Player player) {
        members.add(new TeamPlayer(player));
    }

    public void removeMember(Player player) {
        members.removeIf(p -> p.getPlayer() == player);
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
        return members.size();
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
        List<Player> sentPlayers = new ArrayList<Player>() {

            @Override
            public boolean add(Player player) {
                this.filter();
                return super.add(player);
            }

            @Override
            public int size() {
                this.filter();
                return super.size();
            }

            private void filter() {
                for (Player player : this) {
                    Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                    if (profile.getMatchState() == MatchPlayerState.DEAD) {
                        remove(player);
                    }
                }
            }

        };

        for (TeamPlayer player : this.getMembers()) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player.getPlayer());
            if (!sentPlayers.contains(player.getPlayer())) {
                if (profile.getMatchState() == MatchPlayerState.ALIVE) {
                    sentPlayers.add(player.getPlayer());
                }
            }
        }

        return sentPlayers.size();
    }

}
