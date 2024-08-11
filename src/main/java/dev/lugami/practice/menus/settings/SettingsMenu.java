package dev.lugami.practice.menus.settings;

import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends Menu {

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public SettingsMenu() {
        super("&bSettings", 9 * 4);
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        int slot = 10;
        for (Settings settings : Settings.values()) {
            ItemStack stack = createItem(profile, settings);
            setButton(slot++, new Button(stack, (player1, clickType) -> {
                if (settings.hasPermission(player1)) {
                    profile.getProfileOptions().getSettingsMap().put(settings, !profile.getProfileOptions().getSettingsMap().get(settings));
                    player1.sendMessage(CC.translate(profile.getProfileOptions().getSettingsMap().get(settings) ? "&aEnabled " + settings.getName().toLowerCase() + "." : "&cDisabled " + settings.getName().toLowerCase() + "."));
                } else {
                    player1.sendMessage(CC.translate("&cYou don't have the required permissions for this."));
                }
            }));
        }
    }

    public ItemStack createItem(Profile profile, Settings settings) {
        List<String> lore = new ArrayList<>();
        lore.add("&7" + settings.getDescription());
        lore.add("");
        if (settings.hasPermission(profile.getPlayer())) {
            lore.add(profile.getProfileOptions().getSettingsMap().get(settings) ? "&7▶ &aEnable " + settings.getName() : "  &7Enable " + settings.getName());
            lore.add(!profile.getProfileOptions().getSettingsMap().get(settings) ? "&7▶ &cDisable " + settings.getName() : "  &7Disable " + settings.getName());
        } else {
            lore.add("&cNo permission.");
        }
        return new ItemBuilder(settings.getIcon().clone()).name("&b" + settings.getName()).lore(lore).build();
    }
}
