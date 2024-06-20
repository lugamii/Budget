package dev.lugami.practice.hotbar;

import dev.lugami.practice.utils.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class HotbarItem {

    private final ItemStack itemStack;
    private final Action action;

    public HotbarItem(ItemStack stack) {
        this(stack, player -> {});
    }

    public HotbarItem(ItemStack stack, Action action) {
        this.itemStack = stack;
        this.action = action;
    }

}
