package dev.lugami.practice.utils;

import dev.lugami.practice.Budget;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class InventoryWrapper {

    /**
     *  Gets the underlying Bukkit inventory.
     */
    private final Inventory inventory;

    public Inventory get() {
        return inventory;
    }

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

    public void setContents(ItemStack[] items) {
        inventory.setContents(items);
    }

    public void setArmorContents(ItemStack[] items) {
        if (inventory instanceof PlayerInventory) {
            ((PlayerInventory) inventory).setArmorContents(items);
        } else {
            Budget.getInstance().getLogger().warning("Tried to set armor contents of an inventory that is not an player inventory!");
        }
    }

    /**
     * Clears all items from the inventory.
     */
    public void clear() {
        inventory.clear();
    }

    /**
     * Clears all items from the inventory except whatever is on 'item'.
     */
    public void clearExcept(ItemStack... item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);
            boolean shouldClear = true;

            if (currentItem != null) {
                for (ItemStack is : item) {
                    if (currentItem.isSimilar(is)) {
                        shouldClear = false;
                        break;
                    }
                }
            }

            if (shouldClear) {
                inventory.setItem(i, null);
            }
        }
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
        if (slot > get().getSize()) {
            return inventory.getItem(get().getSize() - 1);
        }
        return inventory.getItem(slot);
    }
}
