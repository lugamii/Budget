package dev.lugami.practice.commands.impl;

import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.menus.LeaderboardsMenu;
import dev.lugami.practice.menus.settings.SettingsMenu;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends CommandBase {

    public LeaderboardCommand() {
        super("leaderboards", new String[]{"lbs", "lb"});
    }

    @Command(name = "", desc = "Opens the leaderboards menu for the player.")
    public void execute(@Sender Player player) {
        new LeaderboardsMenu().open(player);
    }
}
