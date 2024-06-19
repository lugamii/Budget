package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.duel.DuelRequest;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Sender;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DuelCommands extends CommandBase {
    public DuelCommands() {
        super("duel");
    }

    @Command(name = "", desc = "Sends a duel to a player", usage = "<target>")
    public void sendDuel(@Sender Player p1, Player target) {
        if (target == null) {
            p1.sendMessage(CC.translate("&cCould not find that player."));
            return;
        }
        if (target == p1) {
            p1.sendMessage(CC.translate("&cYou cannot duel yourself!"));
            return;
        }
        new DuelKitMenu(p1, target).open(p1);
    }

    @Command(name = "accept", desc = "Accepts a duel")
    public void acceptDuel(@Sender Player player) {
        if (DuelRequest.hasPendingDuelRequest(player)) {
            DuelRequest.getDuelRequest(player).acceptDuelRequest();
        } else {
            player.sendMessage(CC.translate("&cYou don't have a pending duel request."));
        }
    }

    @Command(name = "decline", desc = "Declines a duel")
    public void declineDuel(@Sender Player player) {
        if (DuelRequest.hasPendingDuelRequest(player)) {
            DuelRequest.getDuelRequest(player).declineDuelRequest();
        } else {
            player.sendMessage(CC.translate("&cYou don't have a pending duel request."));
        }
    }

    private static class DuelKitMenu extends Menu {
        private final Player p1;
        private final Player target;

        public DuelKitMenu(Player p1, Player target) {
            super("&cSelect a kit", 27);
            this.p1 = p1;
            this.target = target;
            initialize();
        }

        private void initialize() {
            int slot = 0;
            for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
                setButton(slot++, new Button(
                        new ItemBuilder(kit.getIcon() != null ? kit.getIcon() : new ItemBuilder(Material.DIAMOND_SWORD).build())
                                .name("&c" + kit.getName())
                                .build(),
                        player -> new DuelArenaMenu(kit, target).open(player)
                ));
            }
        }
    }

    private static class DuelArenaMenu extends Menu {
        private final Kit kit;
        private final Player target;

        public DuelArenaMenu(Kit kit, Player target) {
            super("&cSelect an arena", 27);
            this.kit = kit;
            this.target = target;
            initialize();
        }

        private void initialize() {
            int slot = 0;
            for (Arena arena : Budget.getInstance().getArenaStorage().getArenas()) {
                if (arena.getWhitelistedKits().contains(kit.getName())) {
                    setButton(slot++, new Button(
                            new ItemBuilder(Material.PAPER)
                                    .name("&c" + arena.getName())
                                    .build(),
                            p1 -> {
                                new DuelRequest(p1, target, kit, arena).sendDuelRequest();
                                p1.closeInventory();
                            }
                    ));
                }
            }
        }
    }
}
