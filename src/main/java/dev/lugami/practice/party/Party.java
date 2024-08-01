package dev.lugami.practice.party;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.team.Team;
import dev.lugami.practice.utils.CC;
import lombok.Getter;
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
            player.sendMessage(CC.translate("&cThe party has been disbanded."));
            Budget.getInstance().getLobbyStorage().bringToLobby(player);
            this.disbanded = true;
        });
    }

    public void join(Player p) {
        if (PartyInvite.hasInvite(p) && !this.getMembers().contains(p.getUniqueId())) {
            this.addMember(p);
            Budget.getInstance().getPartyStorage().bringToParty(p, this);
            this.sendMessage("&a" + p.getName() + " has joined the party.");
        } else {
           if (!PartyInvite.hasInvite(p)) {
               p.sendMessage(CC.translate("&cYou don't have a invite to this party."));
           } else if (this.getMembers().contains(p.getUniqueId())) {
               p.sendMessage(CC.translate("&cYou are already in a party."));
           }
        }
    }

    public void leave(Player p, boolean silent) {
        if (this.getMembers().contains(p.getUniqueId())) {
            Budget.getInstance().getLobbyStorage().bringToLobby(p);
            if (!silent) this.sendMessage("&c" + p.getName() + " has left the party.");
            if (this.getSize() <= 1 || p == this.getLeader()) this.disband();
            this.removeMember(p);
        } else {
            Budget.getInstance().getLogger().warning("Player tried to leave a party he wasn't in (?)");
        }
    }

    public void leave(Player p) {
        this.leave(p, false);
    }

    public void kick(Player p) {
        this.leave(p, true);
        p.sendMessage(CC.translate("&cYou have been kicked from the party."));
        this.sendMessage("&c" + p.getName() + " was kicked from the party.");
    }

    public int getSize() {
        return this.getMembers().size();
    }
}
