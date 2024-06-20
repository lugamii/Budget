package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.menus.MatchSnapshotMenu;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MatchCommands extends CommandBase {
    public MatchCommands() {
        super("match");
    }

    @Command(name = "inventory", desc = "Opens the match inventory for a player after the match ends.", usage = "<target>")
    public void inventory(@Sender Player player, Player target) {
        MatchSnapshot inv = null;
        for (MatchSnapshot inventory : Budget.getInstance().getMatchStorage().getSnapshots()) {
            if (inventory.getTarget() == target) {
                inv = inventory;
                break;
            }
        }
        if (inv == null || inv.isExpired()) {
            player.sendMessage(ChatColor.RED + "That inventory could not be found: It's either expired, or already non-existent.");
        } else {
            new MatchSnapshotMenu(inv).open(player);
        }

    }
}
