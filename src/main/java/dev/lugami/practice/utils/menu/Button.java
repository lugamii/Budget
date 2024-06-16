package dev.lugami.practice.utils.menu;


import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class Button {

    private final ItemStack itemStack;
    private final ButtonAction action;

    public Button(ItemStack itemStack, ButtonAction action) {
        this.itemStack = itemStack;
        this.action = action;
    }

}
