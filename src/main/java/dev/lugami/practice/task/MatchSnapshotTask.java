package dev.lugami.practice.task;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.utils.CustomBukkitRunnable;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchSnapshotTask extends CustomBukkitRunnable {

    public MatchSnapshotTask() {
        super(CustomBukkitRunnable.Mode.TIMER, CustomBukkitRunnable.Type.ASYNC, 2, 0);
    }

    @Override
    public void run() {
        MatchSnapshot snapshot = null;
        for (MatchSnapshot matchSnapshot : Budget.getInstance().getMatchStorage().getSnapshots()) {
            if ((System.currentTimeMillis() - matchSnapshot.getAddedOn()) / 1000 >= 30) {
                snapshot = matchSnapshot;
                matchSnapshot.setExpired(true);
            }
        }
        if (snapshot != null) Budget.getInstance().getMatchStorage().getSnapshots().remove(snapshot);
    }
}
