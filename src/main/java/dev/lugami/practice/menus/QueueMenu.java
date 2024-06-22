package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.queue.Queue;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QueueMenu extends Menu {

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public QueueMenu() {
        super("&bQueues", 36);
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
            lore.add(CC.translate("&fQueueing: &b" + queue.getPlayers().size()));
            lore.add("");
            lore.add(CC.translate("&eClick to queue!"));
            ItemStack itemStack = queue.getKit().getIcon().clone();
            setButton(slot++, new Button(new ItemBuilder(itemStack).name("&b" + queue.getKit().getName()).lore(lore).build(), player -> {
                queue.add(player);
                player.closeInventory();
            }));
        }
    }
}
