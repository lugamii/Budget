package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.Queue;
import dev.lugami.practice.queue.QueueType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ConcurrentModificationException;
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
            if (kit.isEnabled()) {
                // Unranked queue
                this.queues.add(new Queue(kit, false));
                // Ranked queue (if the kit supports ranked matches)
                if (kit.isRanked()) {
                    this.queues.add(new Queue(kit, true));
                }
            }
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
     * Finds a queue by the associated kit and queue type.
     *
     * @param kit The kit associated with the queue.
     * @return The found queue, or null if not found.
     */
    public Queue findQueue(Kit kit, QueueType queueType) {
        return this.queues.stream().filter(queue -> queue.getKit() == kit && queue.isRanked() == (queueType == QueueType.RANKED)).findFirst().orElse(null);
    }

    /**
     * Finds a queue by the unique identifier (UUID).
     *
     * @param uuid The UUID of the queue.
     * @return The found queue, or null if not found.
     */
    public Queue findQueue(UUID uuid, QueueType queueType) {
        return this.queues.stream().filter(queue -> queue.getId() == uuid && queue.isRanked() == (queueType == QueueType.RANKED)).findFirst().orElse(null);
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

    /**
     * Gets the number of players currently in queue.
     *
     * @param kit       The kit that should be checked for fights.
     * @param queueType The queue type to check.
     * @return The number of players in fights.
     */
    public int getInQueue(Kit kit, QueueType queueType) {
        try {
            int i = 0;
            for (Profile profile : Budget.getInstance().getProfileStorage().getProfiles()) {
                if (profile.getState() == ProfileState.QUEUEING) {
                    Queue queue = this.findQueue(kit, queueType);
                    if (queue.isRanked() && queueType == QueueType.RANKED && queue.getPlayers().contains(profile.getPlayer())) {
                        i++;
                    } else if (!queue.isRanked() && queueType == QueueType.UNRANKED && queue.getPlayers().contains(profile.getPlayer())) {
                        i++;
                    }
                }
            }
            return i;
        } catch (ConcurrentModificationException ex) {
            return 0;
        }
    }

    /**
     * Gets the number of players currently in queue.
     *
     * @return The number of players in fights.
     */
    public int getInQueues() {
        try {
            int i = 0;
            for (Profile profile : Budget.getInstance().getProfileStorage().getProfiles()) {
                if (profile.getState() == ProfileState.QUEUEING) {
                    i++;
                }
            }
            return i;
        } catch (ConcurrentModificationException ex) {
            return 0;
        }
    }

}
