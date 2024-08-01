package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.leaderboards.LeaderboardsEntry;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.types.PartyMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.storage.LeaderboardsStorage;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MatchesMenu extends Menu {
    /**
     * Constructs a new Menu with the specified title and size.
     */
    public MatchesMenu() {
        super("&bMatches", 36);
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        int slot = 10;
        if (Budget.getInstance().getMatchStorage().getRunningMatches().isEmpty()) {
            setButton(slot, new Button(new ItemBuilder(Material.REDSTONE).name("&cNo available matches.").build()));
            return;
        }
        for (Match match : Budget.getInstance().getMatchStorage().getMatches()) {
            if (match.getState() != Match.MatchState.IN_PROGRESS) continue;
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&fDuration: &b" + match.getDuration());
            lore.add("&fKit: &b" + match.getKit().getName());
            lore.add("&fType: &b" + getMatchType(match));
            lore.add("");
            lore.add("&eClick to spectate!");
            setButton(slot++, new Button(new ItemBuilder(match.getKit().getIcon().clone()).name("&a" + match.getTeam1().getLeader().getName() + " &7vs. &c" + match.getTeam2().getLeader().getName()).lore(lore).build(), (player1, clickType) -> {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(player1);
                match.addSpectator(player1, profile.getProfileOptions().getSettingsMap().get(Setting.SILENT_SPECTATE));
            }));
        }
    }

    private String getMatchType(Match match) {
        if (!match.isPartyMatch()) {
            return match.getQueueType() == QueueType.UNRANKED ? "Unranked" : "Ranked";
        } else {
            return ((PartyMatch) match).getType() == PartyMatch.MatchType.SPLIT ? "Party (Split)" : "Party (FFA)";
        }
    }
}