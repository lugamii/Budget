package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.duel.DuelRequest;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DuelKitMenu extends Menu {
    private final Player target;

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public DuelKitMenu(Player target) {
        super("&bSelect a kit", 36);
        this.target = target;
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        int slot = 10;
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            if (kit.isEnabled()) {

                ItemStack itemStack = kit.getIcon().clone();
                setButton(slot++, new Button(
                        new ItemBuilder(itemStack != null ? itemStack : new ItemBuilder(Material.DIAMOND_SWORD).build())
                                .name("&b" + kit.getName())
                                .build(),
                        (player1, clickType) -> {
                            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player1);
                            if (profile.getProfileOptions().getSettingsMap().get(Settings.ARENA_SELECTOR) && Settings.ARENA_SELECTOR.hasPermission(player1)) {
                                new DuelArenaMenu(kit, target).open(player1);
                            } else {
                                new DuelRequest(player1, target, kit, Budget.getInstance().getArenaStorage().getRandomArena(kit)).sendDuelRequest();
                                player1.closeInventory();
                            }
                        }
                ));
            }
        }
    }
}