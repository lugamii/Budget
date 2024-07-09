package dev.lugami.practice.match;

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
