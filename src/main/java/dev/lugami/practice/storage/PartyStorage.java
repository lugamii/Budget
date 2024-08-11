package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.party.Party;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.PlayerUtils;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class PartyStorage {

    private final List<Party> parties = new CopyOnWriteArrayList<>();

    public Party findByUUID(UUID id) {
        return this.parties.stream().filter(party -> party.getUUID() == id || party.getMembers().stream().anyMatch(p -> p.getPlayer().getUniqueId() == id)).findFirst().orElse(null);
    }

    public Party findByPlayer(Player player) {
        return this.parties.stream().filter(party -> party.contains(player)).findFirst().orElse(null);
    }

    /**
     * Brings a player to a party.
     *
     * @param player The player to bring to the party.
     */
    public void bringToParty(Player player, Party party) {
        TaskUtil.runTaskLater(() -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.PARTY);
            profile.setMatchState(MatchPlayerState.NONE);
            profile.setParty(party);
            PlayerUtils.resetPlayer(player);
            Budget.getInstance().getHotbarStorage().resetHotbar(player);
        }, 1L);
    }

}
