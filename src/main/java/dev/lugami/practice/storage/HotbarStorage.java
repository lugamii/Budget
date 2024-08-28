package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.hotbar.HotbarItem;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.menus.LeaderboardsMenu;
import dev.lugami.practice.menus.MatchesMenu;
import dev.lugami.practice.menus.QueueMenu;
import dev.lugami.practice.menus.editor.EditorSelectKitMenu;
import dev.lugami.practice.menus.party.PartyEventsMenu;
import dev.lugami.practice.menus.settings.SettingsMenu;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.utils.ActionUtils;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.InventoryWrapper;
import dev.lugami.practice.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class HotbarStorage {

    private final List<HotbarItem> lobbyItems = Arrays.asList(
            new HotbarItem(new ItemBuilder(Material.IRON_SWORD, true).name("&6Unranked").build(), player -> new QueueMenu().open(player)),
            new HotbarItem(new ItemBuilder(Material.DIAMOND_SWORD, true).name("&6Ranked").build(), player -> new QueueMenu(QueueType.RANKED).open(player)),
            new HotbarItem(new ItemBuilder(Material.REDSTONE_TORCH_ON).name("&6Enable Spectator Mode").build(), player -> Budget.getInstance().getLobbyStorage().bringToLobby(player, true)),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.NAME_TAG).name("&6Party").build(), player -> player.chat("/party create")),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.EMERALD, true).name("&6Leaderboards").build(), player -> new LeaderboardsMenu().open(player)),
            new HotbarItem(new ItemBuilder(Material.WATCH, true).name("&6Settings").build(), player -> new SettingsMenu().open(player)),
            new HotbarItem(new ItemBuilder(Material.BOOK).name("&6Kit Editor").build(), player -> new EditorSelectKitMenu().open(player))
    );

    private final List<HotbarItem> queueItems = Arrays.asList(
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.INK_SACK).name("&cLeave Queue").durability(1).build(), player -> {
                if (Budget.getInstance().getQueueStorage().findQueue(player) != null) {
                    Budget.getInstance().getQueueStorage().findQueue(player).remove(player);
                } else {
                    Budget.getInstance().getLobbyStorage().bringToLobby(player);
                }
            }),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED)
    );

    private final List<HotbarItem> spectatorModeItems = Arrays.asList(
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.COMPASS).name("&6Current Matches").build(), player -> new MatchesMenu().open(player)),
            new HotbarItem(new ItemBuilder(Material.REDSTONE_TORCH_ON).name("&6Disable Spectator Mode").build(), player -> Budget.getInstance().getLobbyStorage().bringToLobby(player, false)),
            new HotbarItem(new ItemBuilder(Material.NETHER_STAR).name("&6Random Match").build(), player -> {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                Match match = Budget.getInstance().getMatchStorage().getRandomMatch();
                if (match == null) {
                    player.sendMessage(CC.translate("&cThere are no matches available for you to spectate."));
                    return;
                }
                match.addSpectator(player, profile.getProfileOptions().getSettingsMap().get(Settings.SILENT_SPECTATE));
            }),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED)
    );

    private final List<HotbarItem> spectatorItems = Arrays.asList(
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.INK_SACK).name("&cLeave Spectator").durability(1).build(), player -> {
                if (Budget.getInstance().getMatchStorage().findMatch(player) != null) {
                    Budget.getInstance().getMatchStorage().findMatch(player).removeSpectator(player, player.hasPermission("budget.staff"));
                } else {
                    Budget.getInstance().getLobbyStorage().bringToLobby(player);
                }
            }),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED)
    );

    private final List<HotbarItem> partyItems = Arrays.asList(
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.GOLD_AXE).name("&6Party Events").build(), player -> {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                if (profile.getParty() != null) {
                    if (profile.getParty().getLeader() == player) {
                        new PartyEventsMenu().open(player);
                    } else {
                        player.sendMessage(CC.translate("&cYou are not the leader!"));
                    }
                } else {
                    Budget.getInstance().getLobbyStorage().bringToLobby(player);
                }
            }),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.INK_SACK).name("&cLeave Party").durability(1).build(), player -> {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                if (profile.getParty() != null) {
                    profile.getParty().leave(player);
                } else {
                    Budget.getInstance().getLobbyStorage().bringToLobby(player);
                }
            }),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED),
            new HotbarItem(new ItemBuilder(Material.AIR).build(), ActionUtils.UNFINISHED)
    );

    /**
     * Gets the list of hotbar items based on the player's state.
     *
     * @param state The profile state of the player.
     * @return The list of hotbar items for the given state.
     */
    public List<HotbarItem> getByState(ProfileState state) {
        switch (state) {
            case LOBBY:
                return this.lobbyItems;
            case QUEUEING:
                return this.queueItems;
            case SPECTATING:
                return this.spectatorItems;
            case PARTY:
                return this.partyItems;
            case LOBBY_SPECTATE:
                return this.spectatorModeItems;
            default:
                return Collections.emptyList();
        }
    }


    /**
     * Resets the hotbar for a given player.
     *
     * @param player The player to reset the hotbar.
     */
    public void resetHotbar(Player player) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        InventoryWrapper wrapper = new InventoryWrapper(player.getInventory());
        wrapper.clear();
        getByState(profile.getState()).forEach(hotbarItem ->
                wrapper.setItem(getByState(profile.getState()).indexOf(hotbarItem), hotbarItem.getItemStack()));
        player.updateInventory();
    }
}
