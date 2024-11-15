package dev.lugami.practice.menus.editor;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorSelectKitMenu extends Menu {

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public EditorSelectKitMenu() {
        super("&6Select a kit", 36);
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        int slot = 10;
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            if (kit.isEnabled() && kit.isEditable()) {
                ItemStack itemStack = kit.getIcon();
                setButton(slot++, new Button(
                        new ItemBuilder(itemStack != null ? itemStack : new ItemBuilder(Material.DIAMOND_SWORD).build())
                                .name("&6" + kit.getName())
                                .build(),
                        (player1, clickType) -> Budget.getInstance().getEditorStorage().bringToEditor(player1, kit)
                ));
            }
        }
    }
}
