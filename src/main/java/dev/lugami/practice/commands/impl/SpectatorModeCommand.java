package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.entity.Player;

public class SpectatorModeCommand extends CommandBase {
    public SpectatorModeCommand() {
        super("specmode", new String[]{"spectatormode"});
    }

    @Command(name = "", desc = "Enters the spectator mode")
    public void execute(@Sender Player player) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);

        if (profile.isBusy()) {
            player.sendMessage(Language.CANNOT_DO_ACTION.format());
            return;
        }

        Budget.getInstance().getLobbyStorage().bringToLobby(player, profile.getState() != ProfileState.LOBBY_SPECTATE);
    }
}
