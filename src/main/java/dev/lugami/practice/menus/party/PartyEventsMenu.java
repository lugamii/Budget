package dev.lugami.practice.menus.party;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PartyEventsMenu extends Menu {
    /**
     * Constructs a new Menu with the specified title and size.
     */
    public PartyEventsMenu() {
        super("&6Party Events", 27);
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        setButton(12, new Button(new ItemBuilder(Material.DIAMOND_AXE).name("&6Split").lore("&7Split your party into 2 teams and fight!").build(), (player1, clickType) -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player1);
            if (profile.getParty() == null) {
                player1.sendMessage(CC.translate("&cYou do not have a party!"));
            } else {
                if (profile.getParty().getSize() <= 1) {
                    player1.sendMessage(CC.translate("&cYour party does not have enough players to start an event."));
                    return;
                }
                new PartySelectKitMenu(Match.MatchType.SPLIT).open(player1);
            }
        }));

        setButton(14, new Button(new ItemBuilder(Material.GOLD_SWORD).name("&6FFA").lore("&7Your party will fight in a free-for-all mode!").build(), (player1, clickType) -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player1);
            if (profile.getParty() == null) {
                player1.sendMessage(CC.translate("&cYou do not have a party!"));
            } else {
                if (profile.getParty().getSize() <= 1) {
                    player1.sendMessage(CC.translate("&cYour party does not have enough players to start an event."));
                    return;
                }
                new PartySelectKitMenu(Match.MatchType.FFA).open(player1);
            }
        }));
    }
}
