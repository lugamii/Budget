package dev.lugami.practice.utils.menu;


import dev.lugami.practice.utils.Action;
import dev.lugami.practice.utils.ButtonAction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter @Setter
public class Button {

    private final ItemStack itemStack;
    private final ButtonAction action;

    @Accessors(fluent = true)
    private boolean shouldCancel = true;

    public Button(ItemStack itemStack, ButtonAction action, boolean shouldCancel) {
        this.itemStack = itemStack;
        this.action = action;
        this.shouldCancel = shouldCancel;
    }

    public Button(ItemStack itemStack, boolean shouldCancel) {
        this.itemStack = itemStack;
        this.action = (player, click) -> {};
        this.shouldCancel = shouldCancel;
    }

    public Button(ItemStack itemStack, ButtonAction action) {
        this.itemStack = itemStack;
        this.action = action;
    }

    public Button(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.action = (player, click) -> {};
    }

}
