package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.queue.Queue;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueMenu extends Menu {

    private final QueueType queueType;

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public QueueMenu() {
        super("&6Queues", 36);
        this.queueType = QueueType.UNRANKED;
    }

    /**
     * Constructs a new Menu with the specified title and size.
     *
     * @param queueType The queue type you're joining.
     */
    public QueueMenu(QueueType queueType) {
        super("&6Queues", 36);
        this.queueType = queueType;
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        int slot = 10;
        for (Queue queue : Budget.getInstance().getQueueStorage().getQueues()) {
            List<String> lore = getQueueLore(queue);
            ItemStack itemStack = queue.getKit().getIcon().clone();
            if (queueType == QueueType.RANKED && !queue.isRanked() || queueType == QueueType.UNRANKED && queue.isRanked()) continue;
            else {
                setButton(slot++, new Button(new ItemBuilder(itemStack).name("&6" + queue.getKit().getName() + (queueType == QueueType.RANKED ? " (Ranked)" : "")).lore(lore).build(), (p1, clickType) -> {
                    if (queueType == QueueType.RANKED && !queue.isRanked() || queueType == QueueType.UNRANKED && queue.isRanked()) {
                        Queue queue1 = Budget.getInstance().getQueueStorage().findQueue(queue.getKit(), queueType);
                        queue1.add(p1, queueType);
                    } else {
                        queue.add(p1, queueType);
                    }
                    p1.closeInventory();
                }));
            }
        }
    }

    private List<String> getQueueLore(Queue queue) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.translate("&fFighting: &6" + Budget.getInstance().getMatchStorage().getInFights(queue.getKit())));
        lore.add(CC.translate("&fQueueing: &6" + Budget.getInstance().getQueueStorage().getInQueue(queue.getKit(), queueType)));
        lore.add("");
        lore.add(CC.translate("&eClick to queue!"));
        return lore;
    }

    @Override
    public List<String> getUpdatedLore(Player player, int slot, ItemStack itemStack) {
        Button button = getButton(slot);
        if (button != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                Queue queue = Budget.getInstance().getQueueStorage().findQueue(Budget.getInstance().getKitStorage().getByName(displayName.replace("&6", "").replace(" (Ranked)", "")), queueType);
                if (queue != null) {
                    return getQueueLore(queue);
                }
            }
        }
        return null;
    }

}
