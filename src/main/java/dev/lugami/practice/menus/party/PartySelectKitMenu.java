package dev.lugami.practice.menus.party;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.team.TeamPlayer;
import dev.lugami.practice.match.types.FFAMatch;
import dev.lugami.practice.match.types.SplitMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.DeduplicatingArrayList;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PartySelectKitMenu extends Menu {
    private final Match.MatchType type;

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public PartySelectKitMenu(Match.MatchType type) {
        super("&bSelect a kit", 36);
        this.type = type;
    }

    @Override
    public void initialize(Player player) {
        this.fillBorder();
        int slot = 10;
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            if (kit.isEnabled() && kit.isParty()) {

                ItemStack itemStack = kit.getIcon().clone();
                setButton(slot++, new Button(
                        new ItemBuilder(itemStack != null ? itemStack : new ItemBuilder(Material.DIAMOND_SWORD).build())
                                .name("&b" + kit.getName())
                                .build(),
                        (p1, clickType) -> {
                            Profile profile = Budget.getInstance().getProfileStorage().findProfile(p1);
                            Arena arena = Budget.getInstance().getArenaStorage().getRandomArena(kit);
                            if (arena != null && profile.getParty() != null) {
                                Match match = null;
                                DeduplicatingArrayList<TeamPlayer> partyMembers = profile.getParty().getMembers();
                                if (this.type == Match.MatchType.FFA) {
                                    match = new FFAMatch(kit, arena, Match.MatchType.FFA, profile.getParty());
                                    for (TeamPlayer player1 : partyMembers) {
                                        ((FFAMatch) match).addPlayerToFFA(player1.getPlayer());
                                    }
                                } else {
                                    match = new SplitMatch(kit, arena, Match.MatchType.SPLIT, profile.getParty());
                                    Collections.shuffle(partyMembers);

                                    List<Player> team1 = new ArrayList<>();
                                    List<Player> team2 = new ArrayList<>();

                                    for (int i = 0; i < partyMembers.size(); i++) {
                                        Player player1 = partyMembers.get(i).getPlayer();
                                        if (i % 2 == 0) {
                                            team1.add(player1);
                                        } else {
                                            team2.add(player1);
                                        }
                                    }

                                    for (Player player1 : team1) {
                                        match.addPlayerToTeam1(player1);
                                    }

                                    for (Player player1 : team2) {
                                        match.addPlayerToTeam2(player1);
                                    }
                                }

                                match.start();
                            } else {
                                if (arena == null) {
                                    p1.sendMessage(CC.translate("&cThere are no arenas."));
                                } else if (profile.getParty() == null) {
                                    p1.sendMessage(CC.translate("&cYou are not in a party."));
                                }
                            }
                        }
                ));
            }
        }
    }
}
