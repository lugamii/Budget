package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.entity.Player;

public class StatsMenu extends Menu {

    public StatsMenu() {
        super("&bStatistics", 36);
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        int slot = 10;
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            if (kit.isEnabled() && kit.isRanked()) {
                setButton(slot++, new Button(new ItemBuilder(kit.getIcon()).name("&b" + kit.getName()).lore("", "&fWon: &b" + profile.getStatistics(kit).getWon(), "&fLost: &b" + profile.getStatistics(kit).getLost(), "&fELO: &b" + profile.getStatistics(kit).getElo()).build()));
            } else if (kit.isEnabled()){
                setButton(slot++, new Button(new ItemBuilder(kit.getIcon()).name("&b" + kit.getName()).lore("", "&fWon: &b" + profile.getStatistics(kit).getWon(), "&fLost: &b" + profile.getStatistics(kit).getLost()).build()));
            }
        }
    }
}
