package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.duel.DuelRequest;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.settings.Setting;
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
                        player1 -> {
                            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                            if (profile.getProfileOptions().getSettingsMap().get(Setting.ARENA_SELECTOR) && Setting.ARENA_SELECTOR.hasPermission(player)) {
                                new DuelArenaMenu(kit, target).open(player);
                            } else {
                                new DuelRequest(player, target, kit, Budget.getInstance().getArenaStorage().getRandomArena(kit)).sendDuelRequest();
                                player.closeInventory();
                            }
                        }
                ));
            }
        }
    }
}