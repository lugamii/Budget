package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
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
        MatchSnapshot inv = Budget.getInstance().getMatchStorage().getSnapshots().stream().filter(snap -> snap.getTarget() == target).findFirst().orElse(null);
        if (inv == null || inv.isExpired()) {
            player.sendMessage(Language.SNAPSHOT_NOT_FOUND.format());
        } else {
            new MatchSnapshotMenu(inv).open(player);
        }

    }
}
