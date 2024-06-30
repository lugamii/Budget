package dev.lugami.practice.party;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Team;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class Party extends Team {

    private final UUID UUID = java.util.UUID.randomUUID();
    private boolean disbanded = false;

    public Party(Player leader) {
        super(leader);
        createParty();
    }

    public void createParty() {
        Budget.getInstance().getPartyStorage().getParties().add(this);
        Budget.getInstance().getPartyStorage().bringToParty(this.getLeader(), this);
        this.getLeader().sendMessage(CC.translate("&aThe party has been created."));
    }

    public void disband() {
        Budget.getInstance().getPartyStorage().getParties().remove(this);
        this.doAction(player -> {
            Budget.getInstance().getLobbyStorage().bringToLobby(player);
            this.disbanded = true;
            player.sendMessage(CC.translate("&aThe party has been disbanded."));
        });
    }

    public void join(Player p) {
        if (PartyInvite.hasInvite(p) && !this.getMembers().contains(p.getUniqueId())) {
            this.addMember(p);
            Budget.getInstance().getPartyStorage().bringToParty(p, this);
            this.sendMessage("&a" + p.getName() + " has joined the party.");
        } else {
           if (!PartyInvite.hasInvite(p)) {
               p.sendMessage("&cYou don't have a invite to this party.");
           } else if (this.getMembers().contains(p.getUniqueId())) {
               p.sendMessage("&cYou are already in a party.");
           }
        }
    }

    public void leave(Player p) {
        if (this.getMembers().contains(p.getUniqueId())) {
            this.removeMember(p);
            Budget.getInstance().getLobbyStorage().bringToLobby(p);
            this.sendMessage("&c" + p.getName() + " has left the party.");
            if (this.getSize() <= 1) this.disband();
        } else {
            Budget.getInstance().getLogger().warning("Player tried to leave a party he wasn't in (?)");
        }
    }
}
