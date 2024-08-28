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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardsMenu extends Menu {
    /**
     * Constructs a new Menu with the specified title and size.
     */
    public LeaderboardsMenu() {
        super("&bLeaderboards", 36);
    }

    private final Map<Kit, List<LeaderboardsEntry>> cachedLeaderboards = new HashMap<>();

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        int slot = 10;
        Budget.getInstance().getLeaderboardsStorage().updateLeaderboards();
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            if (kit.isEnabled() && kit.isRanked()) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                try {
                    if (cachedLeaderboards.get(kit) != LeaderboardsStorage.getKitLeaderboards().get(kit) && LeaderboardsStorage.getKitLeaderboards().get(kit) != null) cachedLeaderboards.put(kit, LeaderboardsStorage.getKitLeaderboards().get(kit));
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
                } catch (Exception e) {
                    if (cachedLeaderboards.get(kit) != null) {
                        int pos = 1;
                        for (LeaderboardsEntry leaderboardsEntry : cachedLeaderboards.get(kit)) {
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
                    } else {
                        lore.add("&fLoading...");
                    }
                }
                setButton(slot++, new Button(new ItemBuilder(kit.getIcon()).name("&b&l" + kit.getName() + " ┃ Top 10").lore(lore).build()));
            }
        }
    }
}
