package dev.lugami.practice.menus.settings;

import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MatchSettingsMenu extends Menu {

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public MatchSettingsMenu() {
        super("&bMatch Settings", 27);
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        int slot = 10;
        for (Setting settings : Setting.values()) {
            if (settings == Setting.ARENA_SELECTOR || settings == Setting.DUEL_REQUESTS || settings == Setting.SCOREBOARD) continue;
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

        setButton(16, new Button(new ItemBuilder(Material.REDSTONE).name("&cBack").lore("&7Goes back to the other menu.", "", "&eClick to go back!").build(), (player1, clickType) -> new SettingsMenu(player1).open(player)));
    }

    public ItemStack createItem(Profile profile, Setting settings) {
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
