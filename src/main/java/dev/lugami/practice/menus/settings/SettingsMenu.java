package dev.lugami.practice.menus.settings;

import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends Menu {

    private final Player player;

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public SettingsMenu(Player p) {
        super("&bSettings", 27);
        this.player = p;
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        int slot = 10;
        for (Setting settings : Setting.values()) {
            ItemStack stack = createItem(profile, settings);
            setButton(slot++, new Button(stack, player1 -> {
                if (settings.hasPermission(player1)) {
                    profile.getProfileOptions().getSettingsMap().put(settings, !profile.getProfileOptions().getSettingsMap().get(settings));
                    player1.sendMessage(CC.translate(profile.getProfileOptions().getSettingsMap().get(settings) ? "&aEnabled " + settings.getName().toLowerCase() + "." : "&cDisabled " + settings.getName().toLowerCase() + "."));
                    this.open(player1);
                } else {
                    player1.sendMessage(CC.translate("&cYou don't have the required permissions for this."));
                }
            }));
        }
    }

    public ItemStack createItem(Profile profile, Setting settings) {
        List<String> lore = new ArrayList<>();
        lore.add("&7" + settings.getDescription());
        lore.add("");
        lore.add(settings.hasPermission(profile.getPlayer()) ? "&7Enabled: " + (profile.getProfileOptions().getSettingsMap().get(settings) ? "&a✔" : "&c✖") : "&cNo permission.");
        return new ItemBuilder(settings.getIcon().clone()).name("&b" + settings.getName()).lore(lore).build();
    }
}
