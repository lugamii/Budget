package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.queue.Queue;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class QueueStorage {

    private final List<Queue> queues = new CopyOnWriteArrayList<>();

    /**
     * Constructor that initializes the queue storage and loads queues from kits.
     */
    public QueueStorage() {
        loadQueues();
    }

    /**
     * Loads queues for each kit from the Budget instance.
     */
    private void loadQueues() {
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            if (kit.isEnabled()) this.queues.add(new Queue(kit));
        }
    }

    /**
     * Finds a queue by the associated kit.
     *
     * @param kit The kit associated with the queue.
     * @return The found queue, or null if not found.
     */
    public Queue findQueue(Kit kit) {
        return this.queues.stream().filter(queue -> queue.getKit() == kit).findFirst().orElse(null);
    }

    /**
     * Finds a queue by the unique identifier (UUID).
     *
     * @param uuid The UUID of the queue.
     * @return The found queue, or null if not found.
     */
    public Queue findQueue(UUID uuid) {
        return this.queues.stream().filter(queue -> queue.getId() == uuid).findFirst().orElse(null);
    }

    /**
     * Finds a queue by the player currently in it.
     *
     * @param player The player in the queue.
     * @return The found queue, or null if not found.
     */
    public Queue findQueue(Player player) {
        return this.queues.stream().filter(queue -> queue.getPlayers().contains(player)).findFirst().orElse(null);
    }


}
