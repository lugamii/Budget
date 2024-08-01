package dev.lugami.practice.profile.editor;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class CustomKitLayout {

    private ItemStack[] inventory = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];

}
