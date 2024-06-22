package dev.lugami.practice.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@Getter
public class InventoryWrapper {

    /**
     * -- GETTER --
     *  Gets the underlying Bukkit inventory.
     *
     * @return The Bukkit inventory.
     */
    private final Inventory inventory;

    /**
     * Constructor to wrap an existing inventory.
     *
     * @param inventory The inventory to wrap.
     */
    public InventoryWrapper(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Sets an item in a specific slot of the inventory.
     *
     * @param slot The slot number to set the item in.
     * @param item The item to set in the slot.
     */
    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    /**
     * Clears all items from the inventory.
     */
    public void clear() {
        inventory.clear();
    }

    /**
     * Fills the entire inventory with a specified item.
     *
     * @param item The item to fill the inventory with.
     */
    public void fill(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
    }

    /**
     * Fills a range of slots in the inventory with a specified item.
     *
     * @param start The starting slot number (inclusive).
     * @param end   The ending slot number (inclusive).
     * @param item  The item to fill the slots with.
     */
    public void fillRange(int start, int end, ItemStack item) {
        for (int i = start; i <= end; i++) {
            inventory.setItem(i, item);
        }
    }

    /**
     * Adds an item to the inventory, placing it in the first available slot.
     *
     * @param item The item to add to the inventory.
     */
    public void addItem(ItemStack item) {
        inventory.addItem(item);
    }

    /**
     * Retrieves an item from a specific slot in the inventory.
     *
     * @param slot The slot number to retrieve the item from.
     * @return The item in the specified slot, or null if the slot is empty.
     */
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    /**
     * Creates an ItemStack with a specified material, amount, display name, and lore.
     *
     * @param material The material of the item.
     * @param amount   The amount of the item.
     * @param name     The display name of the item.
     * @param lore     The lore of the item (optional).
     * @return The created ItemStack.
     */
    public static ItemStack createItem(Material material, int amount, String name, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
