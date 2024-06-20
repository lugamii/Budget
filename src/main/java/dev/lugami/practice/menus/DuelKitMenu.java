package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DuelKitMenu extends Menu {
    private final Player target;

    public DuelKitMenu(Player target) {
        super("&bSelect a kit", 36);
        this.target = target;
        this.initialize();
    }

    @Override
    public void initialize() {
        this.fillBorder();
        int slot = 10;
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            if (kit.isEnabled()) {
                while (getButton(slot) != null) {
                    slot++;
                }
                ItemStack itemStack = kit.getIcon().clone();
                setButton(slot++, new Button(
                        new ItemBuilder(itemStack != null ? itemStack : new ItemBuilder(Material.DIAMOND_SWORD).build())
                                .name("&b" + kit.getName())
                                .build(),
                        player -> new DuelArenaMenu(kit, target).open(player)
                ));
            }
        }
    }
}