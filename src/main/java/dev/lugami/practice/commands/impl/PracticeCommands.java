package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.LocationUtil;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Require;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PracticeCommands extends CommandBase {

    public PracticeCommands() {
        super("practice", new String[]{"budget"});
    }

    @Command(name = "setspawn", aliases = {"setlobby"}, desc = "Sets the lobby location.")
    @Require("budget.management.use")
    public void setSpawn(@Sender Player player) {
        player.chat("/management setspawn");
    }

    @Command(name = "spawn", aliases = {"lobby"}, desc = "Goes to the lobby location.")
    public void spawn(@Sender Player player) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.LOBBY) {
            Budget.getInstance().getLobbyStorage().bringToLobby(player);
        } else {
            player.sendMessage(CC.translate("&cYou cannot do this right now."));
        }
    }
}
