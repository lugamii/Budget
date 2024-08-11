package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.entity.Player;

public class SpectateCommands extends CommandBase {
    public SpectateCommands() {
        super("spectate", new String[] {"spectator", "spec"});
    }

    @Command(name = "", desc = "Spectates a match, specified by a player", usage = "<target>")
    public void spectate(@Sender Player player, Player target) {
        if (target == null) {
            player.sendMessage(Language.NULL_TARGET.format());
            return;
        }
        if (target == player) {
            player.sendMessage(Language.CANNOT_SPECTATE_SELF.format());
            return;
        }
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.isBusy()) {
            player.sendMessage(Language.CANNOT_DO_ACTION.format());
            return;
        }
        Profile profile1 = Budget.getInstance().getProfileStorage().findProfile(target);
        if (!profile1.isFighting()) {
            player.sendMessage(CC.translate("&c" + target.getName() + " is not in a fight."));
            return;
        } else {
            Match match = Budget.getInstance().getMatchStorage().findMatch(target);
            if (match == null) return;
            if (match.getState() == Match.MatchState.ENDED) {
                player.sendMessage(CC.translate("&cThis match is not available anymore."));
            }
            match.addSpectator(player, profile.getProfileOptions().getSettingsMap().get(Settings.SILENT_SPECTATE));
        }
    }

    @Command(name = "leave", desc = "Stops spectating a player.")
    public void leave(@Sender Player player) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (!profile.getState().equals(ProfileState.SPECTATING)) {
            player.sendMessage(CC.translate("&cYou are not spectating a player."));
            return;
        } else {
            if (Budget.getInstance().getMatchStorage().findMatch(player) == null) {
                Budget.getInstance().getLobbyStorage().bringToLobby(player);
            } else {
                Budget.getInstance().getMatchStorage().findMatch(player).removeSpectator(player, profile.getProfileOptions().getSettingsMap().get(Settings.SILENT_SPECTATE));
            }
        }
    }
}
