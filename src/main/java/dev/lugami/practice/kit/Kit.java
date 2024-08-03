package dev.lugami.practice.kit;

import dev.lugami.practice.utils.ItemBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Data
public class Kit {

    private boolean enabled = true;
    private boolean ranked = false;
    private boolean hunger = true;
    private boolean boxing = false;
    private boolean party = false;
    private boolean editable = true;

    private String name;
    private ItemStack icon;
    private ItemStack[] inventory = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];

    public Kit(String name) {
        this.name = name;
        this.icon = new ItemBuilder(Material.DIAMOND_SWORD).build();
    }

}
