package dev.lugami.practice.task;

import dev.lugami.practice.match.types.DefaultMatch;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.queue.Queue;
import dev.lugami.practice.queue.QueueType;
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

            if (queue.isRanked()) {
                matchRankedPlayers(queue, playersToRemove);
            } else {
                matchUnrankedPlayers(queue, playersToRemove);
            }

            queue.getPlayers().removeAll(playersToRemove);
        }
    }

    private void matchRankedPlayers(Queue queue, List<Player> playersToRemove) {
        for (int i = 0; i < queue.getPlayers().size() - 1; i++) {
            Player player1 = queue.getPlayers().get(i);
            for (int j = i + 1; j < queue.getPlayers().size(); j++) {
                Player player2 = queue.getPlayers().get(j);
                Arena arena = Budget.getInstance().getArenaStorage().getRandomArena(queue.getKit());
                if (arena == null) {
                    continue;
                }

                DefaultMatch match = new DefaultMatch(queue.getKit(), arena, QueueType.RANKED);
                match.addPlayerToTeam1(player1);
                match.addPlayerToTeam2(player2);
                match.start();

                playersToRemove.add(player1);
                playersToRemove.add(player2);

                break; // Move to the next player in the outer loop
            }
        }
    }

    private void matchUnrankedPlayers(Queue queue, List<Player> playersToRemove) {
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

            DefaultMatch match = new DefaultMatch(queue.getKit(), arena);
            match.addPlayerToTeam1(player1);
            match.addPlayerToTeam2(player2);
            match.start();

            playersToRemove.add(player1);
            playersToRemove.add(player2);
        }
    }
}
