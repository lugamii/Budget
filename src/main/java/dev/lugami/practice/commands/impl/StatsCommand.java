package dev.lugami.practice.commands.impl;

import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.menus.LeaderboardsMenu;
import dev.lugami.practice.menus.StatsMenu;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.entity.Player;

public class StatsCommand extends CommandBase {

    public StatsCommand() {
        super("stats", new String[]{"stat", "wins"});
    }

    @Command(name = "", desc = "Opens the stats menu for the player.")
    public void execute(@Sender Player player) {
        new StatsMenu().open(player);
    }

}
