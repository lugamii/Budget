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
import dev.lugami.practice.utils.menu.MenuManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class DuelCommands extends CommandBase {
    public DuelCommands() {
        super("duel");
    }


    @Command(name = "", desc = "Sends a duel to a player")
    public void sendDuel(@Sender Player p1, Player target) {
        if (target == null) {
            p1.sendMessage(CC.translate("&cCould not find that player."));
            return;
        }
        if (target == p1) {
            p1.sendMessage(CC.translate("&cYou cannot duel yourself!"));
            return;
        }
        Menu duelKitMenu = new Menu("&cSelect a kit", 27);
        int slot = 0;
        for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
            duelKitMenu.setButton(slot++, new Button(new ItemBuilder(kit.getIcon() == null ? new ItemBuilder(Material.DIAMOND_SWORD).build() : kit.getIcon()).name("&c" + kit.getName()).build(), player -> {
                DuelArenaMenu menu = new DuelArenaMenu(kit, target);
                menu.open(player);
                MenuManager.addMenu(menu);
            }));
        }
        duelKitMenu.open(p1);
        MenuManager.addMenu(duelKitMenu);
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

    @Getter
    private static class DuelArenaMenu extends Menu {

        private static Kit kit = null;
        private static Player target = null;

        public DuelArenaMenu() {
            super("&cSelect a arena", 27);
        }

        public DuelArenaMenu(Kit kit, Player player) {
            super("&cSelect a arena", 27);
            this.kit = kit;
            this.target = player;
            int slot = 0;
            for (Arena arena : Budget.getInstance().getArenaStorage().getArenas()) {
                if (arena.getWhitelistedKits().contains(kit.getName())) {
                    setButton(slot++, new Button(new ItemBuilder(Material.PAPER).name("&c" + arena.getName()).build(), p1 -> {
                        DuelRequest duelRequest = new DuelRequest(player, target, this.kit, arena);
                        duelRequest.sendDuelRequest();
                        player.closeInventory();
                    }));
                }
            }
        }


    }

}
