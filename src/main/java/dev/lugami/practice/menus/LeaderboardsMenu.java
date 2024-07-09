package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.leaderboards.LeaderboardsEntry;
import dev.lugami.practice.storage.LeaderboardsStorage;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardsMenu extends Menu {
    /**
     * Constructs a new Menu with the specified title and size.
     */
    public LeaderboardsMenu() {
        super("&bLeaderboards", 36);
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        int slot = 10;
        Budget.getInstance().getLeaderboardsStorage().updateLeaderboards();
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            if (kit.isEnabled() && kit.isRanked()) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                int pos = 1;
                for (LeaderboardsEntry leaderboardsEntry : LeaderboardsStorage.getKitLeaderboards().get(kit)) {
                    if (leaderboardsEntry.getName() == null || (Integer) leaderboardsEntry.getElo() == null) {
                        continue;
                    }
                    lore.add("&b#" + pos + ": &f" + leaderboardsEntry.getName() + "&7 - &f" + leaderboardsEntry.getElo());
                    pos++;
                }
                while (pos <= 10) {
                    lore.add("&b#" + pos + ": &fNone");
                    pos++;
                }
                setButton(slot++, new Button(new ItemBuilder(kit.getIcon().clone()).name("&b&l" + kit.getName() + " ┃ Top 10").lore(lore).build()));
            }
        }
    }
}
