package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.ItemUtils;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class MatchSnapshotMenu extends Menu {

    private final MatchSnapshot snapshot;

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public MatchSnapshotMenu(MatchSnapshot matchSnapshot) {
        super("&6" + matchSnapshot.getTarget().getName() + "'s inventory", 54);
        this.snapshot = matchSnapshot;
    }

    @Override
    public void initialize(Player player) {
        ItemStack[] fixedContents = ItemUtils.fixInventoryOrder(snapshot.getContents());
        for (int i = 0; i < fixedContents.length; ++i) {
            ItemStack itemStack = fixedContents[i];

            if (itemStack != null) {
                if (itemStack.getType() != Material.AIR) {
                    setButton(i, new Button(itemStack, (player1, clickType) -> {}));
                }
            }
        }

        for (int i = 0; i < snapshot.getArmor().length; ++i) {
            ItemStack itemStack = snapshot.getArmor()[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                setButton(39 - i, new Button(itemStack, (player1, clickType) -> {}));
            }
        }

        MatchSnapshot opp = Budget.getInstance().getMatchStorage().findMatchSnapshot(snapshot.getOpponent());
        setButton(45, new Button(new ItemBuilder(Material.ARROW).name("&6Switch to " + snapshot.getOpponent().getName() + "'s inventory").build(), (player1, clickType) -> new MatchSnapshotMenu(opp).open(player1)));
        setButton(46, Menu.getPlaceholderButton());
        setButton(47, new Button(new ItemBuilder(Material.MELON).name("&6Health: &f" + snapshot.getHealth()).amount(snapshot.getHealth() == 0.0 ? 1 : ((Double) snapshot.getHealth()).intValue()).build()));
        setButton(48, new Button(new ItemBuilder(Material.COOKED_BEEF).name("&6Hunger: &f" + snapshot.getHunger()).amount(snapshot.getHunger() == 0.0 ? 1 : ((Double) snapshot.getHunger()).intValue()).build()));
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (snapshot.getEffects().isEmpty()) {
            lore.add("&cNo effects.");
        } else {
            snapshot.getEffects().forEach(effect -> {
                String name = effect.getType().getName().replace("_", " ");
                lore.add("&7- &6" + name.substring(0, 1).toUpperCase() + name.substring(1) + " " + effect.getAmplifier());
            });
        }
        setButton(49, new Button(new ItemBuilder(Material.BREWING_STAND_ITEM).name("&6Effects").lore(lore).build()));
        lore.clear();
        lore.add("");
        lore.add("&6" + snapshot.getTarget().getName() + " &fhad &6" + snapshot.getRemainingPots() + " &fpot" + (snapshot.getRemainingPots() <= 1 ? "" : "s") + " left.");
        lore.add("&6Potions Thrown: &f" + snapshot.getThrownPots());
        lore.add("&6Potions Missed: &f" + snapshot.getMissedPots());
        lore.add("&6Potions Accuracy: &f" + snapshot.getPotionAccuracy());
        setButton(50, new Button(new ItemBuilder(Material.POTION).durability(16421).amount(snapshot.getRemainingPots() == 0 ? 1 : snapshot.getRemainingPots()).name("&6Potions").lore(lore).build()));
        lore.clear();
        lore.add("");
        lore.add("&6Total Hits: &f" + snapshot.getMatch().getTeam(snapshot.getTarget()).getMember(snapshot.getTarget()).getHits());
        lore.add("&6Critical Hits: &f" + snapshot.getMatch().getTeam(snapshot.getTarget()).getMember(snapshot.getTarget()).getCrits());
        lore.add("&6Blocked Hits: &f" + snapshot.getMatch().getTeam(snapshot.getTarget()).getMember(snapshot.getTarget()).getBlocked());


        setButton(51, new Button(new ItemBuilder(Material.PAPER).name("&6Statistics").lore(lore).build()));

        setButton(52, Menu.getPlaceholderButton());
        setButton(53, new Button(new ItemBuilder(Material.ARROW).name("&6Switch to " + snapshot.getOpponent().getName() + "'s inventory").build(), (player1, clickType) -> new MatchSnapshotMenu(opp).open(player1)));
    }
}
