package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.queue.Queue;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QueueMenu extends Menu {

    private final QueueType queueType;

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public QueueMenu() {
        super("&bQueues", 36);
        this.queueType = QueueType.UNRANKED;
    }

    /**
     * Constructs a new Menu with the specified title and size.
     *
     * @param queueType The queue type you're joining.
     */
    public QueueMenu(QueueType queueType) {
        super("&bQueues", 36);
        this.queueType = queueType;
    }

    @Override
    public void initialize() {
        this.fillBorder();
        int slot = 10;
        for (Queue queue : Budget.getInstance().getQueueStorage().getQueues()) {

            while (getButton(slot) != null) {
                slot++;
            }
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fFighting: &b" + Budget.getInstance().getMatchStorage().getInFights(queue.getKit())));
            lore.add(CC.translate("&fQueueing: &b" + Budget.getInstance().getQueueStorage().getInQueue(queue.getKit(), queueType)));
            lore.add("");
            lore.add(CC.translate("&eClick to queue!"));
            ItemStack itemStack = queue.getKit().getIcon().clone();
            if (queueType == QueueType.RANKED && !queue.isRanked() || queueType == QueueType.UNRANKED && queue.isRanked()) continue;
            else {
                setButton(slot++, new Button(new ItemBuilder(itemStack).name("&b" + queue.getKit().getName() + (queueType == QueueType.RANKED ? " (Ranked)" : "")).lore(lore).build(), player -> {
                    if (queueType == QueueType.RANKED && !queue.isRanked() || queueType == QueueType.UNRANKED && queue.isRanked()) {
                        Queue queue1 = Budget.getInstance().getQueueStorage().findQueue(queue.getKit(), queueType);
                        queue1.add(player, queueType);
                    } else {
                        queue.add(player, queueType);
                    }
                    player.closeInventory();
                }));
            }
        }
    }
}
