package dev.lugami.practice.menus.party;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.types.PartyMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;

public class PartyEventsMenu extends Menu {
    /**
     * Constructs a new Menu with the specified title and size.
     */
    public PartyEventsMenu() {
        super("&bParty Events", 27);
    }

    @Override
    public void initialize() {
        this.fillBorder();
        setButton(12, new Button(new ItemBuilder(Material.DIAMOND_AXE).name("&bSplit").lore("&7Split your party into 2 teams and fight!").build(), player -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            if (profile.getParty() == null) {
                player.sendMessage(CC.translate("&cYou do not have a party!"));
            } else {
                if (profile.getParty().getSize() <= 1) {
                    player.sendMessage(CC.translate("&cYour party does not have enough players to start an event."));
                    return;
                }
                new SelectKitMenu(PartyMatch.MatchType.SPLIT).open(player);
            }
        }));

        setButton(14, new Button(new ItemBuilder(Material.GOLD_SWORD).name("&bFFA").lore("&7Your party will fight in a free-for-all mode!").build(), player -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            if (profile.getParty() == null) {
                player.sendMessage(CC.translate("&cYou do not have a party!"));
            } else {
                if (profile.getParty().getSize() <= 1) {
                    player.sendMessage(CC.translate("&cYour party does not have enough players to start an event."));
                    return;
                }
                new SelectKitMenu(PartyMatch.MatchType.FFA).open(player);
            }
        }));
    }
}
