package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.party.Party;
import dev.lugami.practice.party.PartyInvite;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.entity.Player;

public class PartyCommands extends CommandBase {
    public PartyCommands() {
        super("party", new String[] {"p"});
    }

    @Command(name = "create", desc = "Creates a party.")
    public void create(@Sender Player sender) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(sender);
        if (profile.isBusy()) {
            sender.sendMessage(CC.translate("&cYou cannot do this right now."));
        } else {
            profile.setParty(new Party(sender));
        }
    }

    @Command(name = "join", desc = "Joins a party.")
    public void join(@Sender Player sender) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(sender);
        if (PartyInvite.hasInvite(sender)) {
            PartyInvite invite = PartyInvite.getPartyRequest(sender);
            if (invite == null || invite.getParty() == null) {
                Budget.getInstance().getLogger().warning("Party was null on " + invite.toString());
                return;
            }
            invite.getParty().join(sender);
        } else {
            sender.sendMessage(CC.translate("&cYou don't have a party invite."));
        }
    }

    @Command(name = "leave", desc = "Leave a party.")
    public void leave(@Sender Player sender) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(sender);
        if (!profile.isInParty()) {
            sender.sendMessage(CC.translate("&cYou are not in a party."));
        } else {
            profile.getParty().leave(sender);
        }
    }

    @Command(name = "join", desc = "Joins a party.", usage = "<party>")
    public void join(@Sender Player sender, Player target) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(sender);
        if (target == null) {
            sender.sendMessage(CC.translate("&cCould not find that player."));
            return;
        }
        if (PartyInvite.hasInvite(sender)) {
            PartyInvite invite = PartyInvite.getPartyRequest(sender);
            if (invite == null || invite.getParty() == null) {
                Budget.getInstance().getLogger().warning("Party was null on " + invite.toString());
                return;
            }
            if (invite.getParty().getLeader() == target) {
                invite.getParty().join(sender);
            } else {
                // ???
            }
        } else {
            sender.sendMessage(CC.translate("&cYou don't have a party invite."));
        }
    }

    @Command(name = "invite", desc = "Invites a player to a party.", usage = "<target>")
    public void invite(@Sender Player sender, Player target) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(sender);
        if (!profile.isInParty()) {
            sender.sendMessage(CC.translate("&cYou are not in a party."));
        } else {
            PartyInvite partyInvite = new PartyInvite(profile.getParty(), target);
            partyInvite.send();
        }
    }

    @Command(name = "disband", desc = "Disbands a party.")
    public void disband(@Sender Player sender) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(sender);
        if (!profile.isInParty()) {
            sender.sendMessage(CC.translate("&cYou are not in a party."));
        } else {
            Party party = profile.getParty();
            if (party == null) {
                sender.sendMessage(CC.translate("&cYou are not in a party."));
                return;
            }
            if (party.getLeader() == sender) {
                party.disband();
            } else {
                sender.sendMessage(CC.translate("&cYou cannot do this, as you're not the leader."));
            }
        }
    }


}