package dev.lugami.practice.menus;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.duel.DuelRequest;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DuelArenaMenu extends Menu {
    private final Kit kit;
    private final Player target;

    public DuelArenaMenu(Kit kit, Player target) {
        super("&bSelect an arena", 36);
        this.kit = kit;
        this.target = target;
        this.initialize();
    }

    @Override
    public void initialize() {
        this.fillBorder();
        int slot = 10;
        for (Arena arena : Budget.getInstance().getArenaStorage().getArenas()) {
            if (arena.isEnabled() && arena.getWhitelistedKits().contains(kit.getName())) {
                while (getButton(slot) != null) {
                    slot++;
                }
                setButton(slot++, new Button(
                        new ItemBuilder(Material.PAPER)
                                .name("&b" + arena.getName())
                                .build(),
                        player -> {
                            new DuelRequest(player, target, kit, arena).sendDuelRequest();
                            player.closeInventory();
                        }
                ));
            }
        }
    }
}