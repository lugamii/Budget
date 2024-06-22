package dev.lugami.practice.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fluent builder for creating and customizing ItemStacks in Bukkit/Spigot.
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private final boolean unbreakable;

    /**
     * Constructs an ItemBuilder with the given Material.
     *
     * @param material The material type of the ItemStack to create.
     */
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.unbreakable = false;
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack.
     *
     * @param itemStack The ItemStack to modify.
     */
    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.unbreakable = false;
    }

    /**
     * Constructs an ItemBuilder with the given Material.
     *
     * @param material The material type of the ItemStack to create.
     * @param unbreak If it's unbreakable or not
     */
    public ItemBuilder(Material material, boolean unbreak) {
        this.itemStack = new ItemStack(material);
        this.unbreakable = unbreak;
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack.
     *
     * @param itemStack The ItemStack to modify.
     * @param unbreak If it's unbreakable or not
     */
    public ItemBuilder(ItemStack itemStack, boolean unbreak) {
        this.itemStack = itemStack;
        this.unbreakable = unbreak;
    }

    /**
     * Sets the amount of items in the ItemStack.
     *
     * @param amount The amount of items.
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * Sets the display name of the ItemStack.
     *
     * @param name The display name to set.
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder name(String name) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Adds lore lines to the ItemStack.
     *
     * @param lore The lore lines to add.
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder lore(String... lore) {
        ItemMeta meta = getItemMeta();
        List<String> coloredLore = meta.getLore();

        if (coloredLore == null) {
            coloredLore = new ArrayList<>();
        }

        coloredLore.addAll(CC.translate(Arrays.asList(lore)));
        meta.setLore(coloredLore);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Adds lore lines to the ItemStack.
     *
     * @param lore The list of lore lines to add.
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        List<String> coloredLore = meta.getLore();

        if (coloredLore == null) {
            coloredLore = new ArrayList<>();
        }

        coloredLore.addAll(CC.translate(lore));
        meta.setLore(coloredLore);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Clears all lore lines from the ItemStack.
     *
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder clearLore() {
        ItemMeta meta = getItemMeta();
        meta.setLore(null);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Enchants the ItemStack with the specified Enchantment at the given level.
     *
     * @param enchantment The enchantment to apply.
     * @param level       The level of the enchantment.
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Enchants the ItemStack with the specified Enchantment at level 1.
     *
     * @param enchantment The enchantment to apply.
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    /**
     * Clears all enchantments from the ItemStack.
     *
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder clearEnchantments() {
        itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment);
        return this;
    }

    /**
     * Sets the durability of the ItemStack.
     *
     * @param durability The durability value to set.
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder durability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }

    /**
     * Sets the color of the leather armor ItemStack.
     *
     * @param color The color to set.
     * @return The ItemBuilder instance for method chaining.
     */
    public ItemBuilder color(Color color) {
        if (itemStack.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
            meta.setColor(color);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Builds the final ItemStack configured by this ItemBuilder.
     *
     * @return The constructed ItemStack.
     */
    public ItemStack build() {
        if (itemStack.getType() != Material.AIR) {
            ItemMeta meta = getItemMeta();
            meta.spigot().setUnbreakable(unbreakable);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    private ItemMeta getItemMeta() {
        return itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
    }
}
