package dev.lugami.practice.menus;

import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.utils.ItemUtils;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MatchSnapshotMenu extends Menu {

    private final MatchSnapshot snapshot;

    public MatchSnapshotMenu(MatchSnapshot matchSnapshot) {
        super("&b" + matchSnapshot.getTarget().getName() + "'s inventory", 45);
        this.snapshot = matchSnapshot;
        this.initialize();
    }

    @Override
    public void initialize() {
        ItemStack[] fixedContents = ItemUtils.fixInventoryOrder(snapshot.getContents());
        for (int i = 0; i < fixedContents.length; ++i) {
            ItemStack itemStack = fixedContents[i];

            if (itemStack != null) {
                if (itemStack.getType() != Material.AIR) {
                    setButton(i, new Button(itemStack, player -> {}));
                }
            }
        }

        for (int i = 0; i < snapshot.getArmor().length; ++i) {
            ItemStack itemStack = snapshot.getArmor()[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                setButton(39 - i, new Button(itemStack, player -> {}));
            }
        }
    }
}
