package dev.lugami.practice.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@AllArgsConstructor
public enum Setting {

    SCOREBOARD("Scoreboard", "Toggles your own scoreboard.", "", true, new ItemStack(Material.PAINTING)),
    DUEL_REQUESTS("Duel Requests", "Toggles people sending duels to you.", "", true, new ItemStack(Material.GOLD_SWORD)),
    ARENA_SELECTOR("Arena Selector", "Toggles selecting arenas in duel requests.", "budget.selector.arena", true, new ItemStack(Material.PAPER));

    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String permission;
    @Getter
    private final boolean defaultToggled;
    private final ItemStack icon;

    public ItemStack getIcon() {
        return icon.clone();
    }

    public boolean hasPermission(Player player) {
        return permission.equalsIgnoreCase("") || player.hasPermission(permission);
    }
}
