package dev.lugami.practice.duel;

import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.match.types.DefaultMatch;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.Clickable;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class DuelRequest {

    private static final Map<UUID, DuelRequest> duelRequests = new HashMap<>();

    private final Player requester;
    private final Player target;
    private final Kit kit;
    private final Arena arena;
    private final long requestTime;

    public DuelRequest(Player requester, Player target, Kit kit, Arena arena) {
        this.requester = requester;
        this.target = target;
        this.kit = kit;
        this.arena = arena;
        this.requestTime = System.currentTimeMillis();
    }

    /**
     * Sends a duel request from one player to another.
     */
    public void sendDuelRequest() {
        DuelRequest duelRequest = new DuelRequest(requester, target, kit, arena);
        duelRequests.put(target.getUniqueId(), duelRequest);
        requester.sendMessage(CC.translate("&aDuel request sent successfully to " + target.getName() + " on arena " + arena.getName() + " with kit " + kit.getName() + "!"));
        target.sendMessage(CC.translate("&b" + requester.getName() + " &fhas challenged you to a duel with kit &b" + kit.getName() + " &fin arena &b" + arena.getName() + "."));
        Clickable clickable = new Clickable();
        clickable.add("&a[Click to accept]", "&aClick to accept " + requester.getName() + "'s duel!", "/duel accept");
        clickable.add(" &for ");
        clickable.add("&c[Click to decline]", "&cClick to decline " + requester.getName() + "'s duel!", "/duel decline");
        clickable.sendToPlayer(target);
    }

    /**
     * Accepts a duel request and starts the match if the request is valid and not expired.
     */
    public void acceptDuelRequest() {
        if (System.currentTimeMillis() - getRequestTime() < 30000) { // Request is valid for 60 seconds
            Player requester = getRequester();
            Player target = getTarget();

            if (requester != null) {
                DefaultMatch match = new DefaultMatch(getKit(), getArena());
                match.addPlayerToTeam1(requester);
                match.addPlayerToTeam2(target);
                match.start();
            } else {
                target.sendMessage(ChatColor.RED + "The requester is no longer online.");
            }
        } else {
            requester.sendMessage(ChatColor.RED + "The duel request has expired.");
            target.sendMessage(ChatColor.RED + "The duel request has expired.");
        }
    }

    /**
     * Declines a duel request and notifies both players.
     */
    public void declineDuelRequest() {
        Player requester = getRequester();
        if (requester != null) {
            requester.sendMessage(ChatColor.RED + target.getName() + " has declined your duel request.");
        }
        target.sendMessage(ChatColor.GREEN + "You have declined the duel request from " + (requester != null ? requester.getName() : "someone") + ".");
    }

    /**
     * Checks if a player has a pending duel request.
     *
     * @param target the player to check
     * @return true if the player has a pending duel request, false otherwise
     */
    public static boolean hasPendingDuelRequest(Player target) {
        return duelRequests.containsKey(target.getUniqueId());
    }

    /**
     * Returns a DuelRequest from a target.
     *
     * @param target the player to check
     * @return null if there is no duel request, DuelRequest otherwise
     */
    public static DuelRequest getDuelRequest(Player target) {
        if (hasPendingDuelRequest(target)) {
            return duelRequests.get(target.getUniqueId());
        } else {
            return null;
        }
    }
}
