package dev.lugami.practice.utils.menu;


import dev.lugami.practice.utils.Action;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class Button {

    private final ItemStack itemStack;
    private final Action action;

    public Button(ItemStack itemStack, Action action) {
        this.itemStack = itemStack;
        this.action = action;
    }

    public Button(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.action = player -> {};
    }

}
