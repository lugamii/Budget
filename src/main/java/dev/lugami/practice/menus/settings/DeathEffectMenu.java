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

public class DeathEffectMenu extends Menu {

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public DeathEffectMenu() {
        super("&6Settings", 9 * 4);
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        int y = 1;
        int x = 1;
        for (Settings settings : Settings.values()) {
            if (settings == Settings.LIGHTNING || settings == Settings.EXPLOSION) {
                ItemStack stack = createItem(profile, settings);
                setButton(getSlot(x++, y), new Button(stack, (player1, clickType) -> {
                    if (settings.hasPermission(player1)) {
                        profile.getProfileOptions().setToggled(settings, !profile.getProfileOptions().isToggled(settings));
                        player1.sendMessage(CC.translate(profile.getProfileOptions().isToggled(settings) ? "&aEnabled " + settings.getName().toLowerCase() + "." : "&cDisabled " + settings.getName().toLowerCase() + "."));
                    } else {
                        player1.sendMessage(CC.translate("&cYou don't have the required permissions for this."));
                    }
                }));
                if (x == 8) {
                    y++;
                    x = 1;
                }
            }
        }
    }


    public ItemStack createItem(Profile profile, Settings settings) {
        List<String> lore = new ArrayList<>();
        lore.add("&7" + settings.getDescription());
        if (settings.isBeta()) {
            lore.add("");
            lore.add("&6&lNOTE: &fThis feature is currently being tested,");
            lore.add("&fso please report any bugs you find with it!");
        }
        lore.add("");
        if (settings.hasPermission(profile.getPlayer())) {
            lore.add(profile.getProfileOptions().getSettingsMap().get(settings) ? "&7➜ &aEnabled" : "  &7Enabled");
            lore.add(!profile.getProfileOptions().getSettingsMap().get(settings) ? "&7➜ &cDisabled" : "  &7Disabled");
        } else {
            lore.add("&cNo permission.");
        }
        return new ItemBuilder(settings.getIcon().clone()).name("&6" + settings.getName() + (settings.isBeta() ? " &7(Unfinished)" : "")).lore(lore).build();
    }
}
