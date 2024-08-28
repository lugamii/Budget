package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.menus.settings.SettingsMenu;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.entity.Player;

public class SettingsCommand extends CommandBase {

    public SettingsCommand() {
        super("settings", new String[]{"options"});
    }

    @Command(name = "", desc = "Opens the settings menu for the player.")
    public void execute(@Sender Player player) {
        new SettingsMenu().open(player);
    }

}
