package dev.lugami.practice.match.team;

import org.bukkit.entity.Player;

public class FFATeam extends Team {

    public FFATeam(Player leader) {
        super(leader);
    }

    @Override
    public int getSize() {
        return getMembers().size();
    }

}
