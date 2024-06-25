package dev.lugami.practice.leaderboards;

import lombok.Data;

@Data
public class LeaderboardsEntry {

    private String name;
    private int elo = 1000;

    public LeaderboardsEntry(String name, int elo) {
        this.name = name;
        this.elo = elo;
    }

    public LeaderboardsEntry() {
        // This is empty.
    }

}
