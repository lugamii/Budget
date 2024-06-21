package dev.lugami.practice.task;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.queue.Queue;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class QueueTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Queue queue : Budget.getInstance().getQueueStorage().getQueues()) {
            if (queue.getPlayers().isEmpty() || queue.getPlayers().size() == 1) {
                continue;
            }

            List<Player> playersToRemove = new ArrayList<>();

            for (int i = 0; i < queue.getPlayers().size() - 1; i += 2) {
                Player player1 = queue.getPlayers().get(i);
                Player player2 = queue.getPlayers().get(i + 1);

                if (player1 == null || player2 == null) {
                    continue;
                }

                Arena arena = Budget.getInstance().getArenaStorage().getRandomArena(queue.getKit());
                if (arena == null) {
                    continue;
                }

                Match match = new Match(queue.getKit(), arena);
                match.addPlayerToTeam1(player1);
                match.addPlayerToTeam2(player2);
                match.start();

                playersToRemove.add(player1);
                playersToRemove.add(player2);
            }

            queue.getPlayers().removeAll(playersToRemove);
        }
    }
}
