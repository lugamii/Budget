package dev.lugami.practice.party;

import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.Clickable;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PartyInvite {

    private static final Map<UUID, PartyInvite> partyInvites = new HashMap<>();

    private final long requestTime;
    private final Party party;
    private final Player target;
    private final UUID uuid = UUID.randomUUID();

    public PartyInvite(@NonNull Party party, Player player) {
        this.party = party;
        this.target = player;
        this.requestTime = System.currentTimeMillis();
        this.partyInvites.put(target.getUniqueId(), this);
    }

    public void send() {
        target.sendMessage(CC.translate("&aYou have been invited to " + party.getLeader().getName() + "'s party."));
        Clickable clickable = new Clickable();
        clickable.add(CC.translate("&aClick here to accept."), CC.translate("&aJoins " + party.getLeader().getName() + "'s party."), "/party join " + getParty().getLeader().getName());
        clickable.sendToPlayer(target);
    }

    /**
     * Checks if a player has a pending party invite.
     *
     * @param target the player to check
     * @return true if the player has a party invite, false otherwise
     */
    public static boolean hasInvite(Player target) {
        return partyInvites.containsKey(target.getUniqueId());
    }

    /**
     * Returns a PartyInvite from a target.
     *
     * @param target the player to check
     * @return null if there is no duel request, PartyInvite otherwise
     */
    public static PartyInvite getPartyRequest(Player target) {
        if (hasInvite(target)) {
            return partyInvites.get(target.getUniqueId());
        } else {
            return null;
        }
    }

}
