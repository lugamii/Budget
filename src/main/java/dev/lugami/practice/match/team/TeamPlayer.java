package dev.lugami.practice.match.team;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public class TeamPlayer {

    private final Player player;
    private int hits = 0;
    private int blocked = 0;
    private int crits = 0;
    private int thrownPots = 0;
    private int missedPots = 0;

    public void hit() {
        hits++;
    }

    public void block() {
        blocked++;
    }

    public void crit() {
        crits++;
    }

    public void miss() {
        missedPots++;
    }

    public void pot() {
        thrownPots++;
    }

}
