package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.duel.DuelRequest;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.menus.DuelKitMenu;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.settings.Setting;
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
            p1.sendMessage(Language.NULL_TARGET.format());
            return;
        }
        if (target == p1) {
            p1.sendMessage(CC.translate("&cYou cannot duel yourself!"));
            return;
        }

        Profile prf1 = Budget.getInstance().getProfileStorage().findProfile(p1);

        if (prf1.isBusy()) {
            p1.sendMessage(Language.CANNOT_DO_ACTION.format());
            return;
        }

        Profile profile = Budget.getInstance().getProfileStorage().findProfile(target);
        if (!profile.getProfileOptions().getSettingsMap().get(Setting.DUEL_REQUESTS)) {
            p1.sendMessage(CC.translate("&cThis player is not accepting duel requests."));
            return;
        }

        if (!profile.isBusy()) {
            new DuelKitMenu(target).open(p1);
        } else {
            p1.sendMessage(CC.translate("&c" + target.getName() + " is not in the lobby."));
        }
    }

    @Command(name = "accept", desc = "Accepts a duel")
    public void acceptDuel(@Sender Player player) {
        Profile prf1 = Budget.getInstance().getProfileStorage().findProfile(player);

        if (prf1.isBusy()) {
            player.sendMessage(Language.CANNOT_DO_ACTION.format());
            return;
        }

        if (DuelRequest.hasPendingDuelRequest(player)) {
            Profile prf2 = Budget.getInstance().getProfileStorage().findProfile(DuelRequest.getDuelRequest(player).getRequester());
            if (prf2.isBusy()) {
                player.sendMessage(CC.translate("&c" + DuelRequest.getDuelRequest(player).getRequester().getName() + " is not in the lobby."));
                return;
            }
            DuelRequest.getDuelRequest(player).acceptDuelRequest();
        } else {
            player.sendMessage(CC.translate("&cYou don't have a pending duel request."));
        }
    }

    @Command(name = "decline", desc = "Declines a duel")
    public void declineDuel(@Sender Player player) {
        Profile prf1 = Budget.getInstance().getProfileStorage().findProfile(player);

        if (prf1.isBusy()) {
            player.sendMessage(Language.CANNOT_DO_ACTION.format());
            return;
        }

        if (DuelRequest.hasPendingDuelRequest(player)) {
            Profile prf2 = Budget.getInstance().getProfileStorage().findProfile(DuelRequest.getDuelRequest(player).getRequester());
            if (prf2.isBusy()) {
                player.sendMessage(CC.translate("&c" + DuelRequest.getDuelRequest(player).getRequester().getName() + " is not in the lobby."));
                return;
            }
            DuelRequest.getDuelRequest(player).declineDuelRequest();
        } else {
            player.sendMessage(CC.translate("&cYou don't have a pending duel request."));
        }
    }

}
