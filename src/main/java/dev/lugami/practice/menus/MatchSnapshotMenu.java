package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.ItemUtils;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MatchSnapshotMenu extends Menu {

    private final MatchSnapshot snapshot;

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public MatchSnapshotMenu(MatchSnapshot matchSnapshot) {
        super("&b" + matchSnapshot.getTarget().getName() + "'s inventory", 45);
        this.snapshot = matchSnapshot;
    }

    @Override
    public void initialize(Player player) {
        ItemStack[] fixedContents = ItemUtils.fixInventoryOrder(snapshot.getContents());
        for (int i = 0; i < fixedContents.length; ++i) {
            ItemStack itemStack = fixedContents[i];

            if (itemStack != null) {
                if (itemStack.getType() != Material.AIR) {
                    setButton(i, new Button(itemStack, player1 -> {}));
                }
            }
        }

        for (int i = 0; i < snapshot.getArmor().length; ++i) {
            ItemStack itemStack = snapshot.getArmor()[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                setButton(39 - i, new Button(itemStack, player1 -> {}));
            }
        }
        MatchSnapshot opp = Budget.getInstance().getMatchStorage().findMatchSnapshot(snapshot.getOpponent());
        setButton(44, new Button(new ItemBuilder(Material.PAPER).name("&bSwitch to " + snapshot.getOpponent().getName() + "'s inventory").build(), player1 -> new MatchSnapshotMenu(opp).open(player1)));
    }
}
