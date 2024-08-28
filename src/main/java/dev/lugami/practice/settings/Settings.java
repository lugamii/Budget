package dev.lugami.practice.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor @RequiredArgsConstructor
public enum Settings {

    SCOREBOARD("Scoreboard", "Toggles your own scoreboard.", "", true, new ItemStack(Material.PAINTING)),
    DUEL_REQUESTS("Duel Requests", "Toggles people sending duels to you.", "", true, new ItemStack(Material.GOLD_SWORD)),
    PARTY_REQUESTS("Party Requests", "Toggles people sending party invites to you.", "", true, new ItemStack(Material.NAME_TAG)),
    ARENA_SELECTOR("Arena Selector", "Toggles selecting arenas in duel requests.", "budget.selector.arena", true, new ItemStack(Material.PAPER)),
    SILENT_SPECTATE("Silent Spectate", "Toggles silently spectating a player.", "budget.spectator.silent", false, new ItemStack(Material.FEATHER)),
    ALLOW_SPECTATORS("Match Spectators", "Toggles people spectating your matches.", "", true, new ItemStack(Material.DIAMOND)),
    LOBBY_MUSIC("Lobby Music", "Toggles the lobby music for you.", "budget.lobby.music", false, new ItemStack(Material.JUKEBOX), true),
    EXPLOSION("Death Explosion", "Toggles a explosion effect when you kill someone.", "", false, new ItemStack(Material.TNT)),
    LIGHTNING("Death Lightning", "Toggles a lightning effect when you kill someone.", "", true, new ItemStack(Material.BLAZE_ROD)),
    DEATHEFFECT("Death Effect", "Toggles a death effect when you kill someone.", "", true, new ItemStack(Material.SKULL_ITEM));

    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String permission;
    @Getter
    private final boolean defaultToggled;
    private final ItemStack icon;

    @Getter
    private boolean beta = false;

    public ItemStack getIcon() {
        return icon.clone();
    }

    public boolean hasPermission(Player player) {
        return permission.equalsIgnoreCase("") || player.hasPermission(permission);
    }
}
