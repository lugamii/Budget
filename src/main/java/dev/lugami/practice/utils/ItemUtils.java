package dev.lugami.practice.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    // Serialize ItemStack array
    public static String serializeInventory(ItemStack[] source) {
        StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : source) {
            builder.append(serializeItemStack(itemStack));
            builder.append(";");
        }

        return builder.toString();
    }

    // Deserialize ItemStack array
    public static ItemStack[] deserializeInventory(String source) {
        List<ItemStack> items = new ArrayList<>();
        String[] split = source.split(";");

        for (String piece : split) {
            items.add(deserializeItemStack(piece));
        }

        return items.toArray(new ItemStack[0]);
    }

    // Serialize List of PotionEffects
    public static String serializeEffects(List<PotionEffect> source) {
        StringBuilder builder = new StringBuilder();

        for (PotionEffect effect : source) {
            builder.append(serializeEffect(effect));
            builder.append(";");
        }

        return builder.toString();
    }

    // Deserialize List of PotionEffects
    public static List<PotionEffect> deserializeEffects(String source) {
        List<PotionEffect> effects = new ArrayList<>();
        String[] split = source.split(";");

        for (String piece : split) {
            effects.add(deserializeEffect(piece));
        }

        return effects;
    }

    // Serialize single ItemStack
    public static String serializeItemStack(ItemStack item) {
        StringBuilder builder = new StringBuilder();

        if (item == null) {
            return "null";
        }

        builder.append("t@").append(item.getType().name());

        if (item.getDurability() != 0) {
            builder.append(":d@").append(item.getDurability());
        }

        if (item.getAmount() != 1) {
            builder.append(":a@").append(item.getAmount());
        }

        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        if (!enchantments.isEmpty()) {
            builder.append(":e@");
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                builder.append(entry.getKey().getName()).append("@").append(entry.getValue()).append(";");
            }
            // Remove the last semicolon
            builder.deleteCharAt(builder.length() - 1);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                builder.append(":dn@").append(meta.getDisplayName());
            }
            if (meta.hasLore()) {
                builder.append(":l@").append(meta.getLore());
            }
        }

        return builder.toString();
    }

    // Deserialize single ItemStack
    public static ItemStack deserializeItemStack(String source) {
        ItemStack itemStack = new ItemStack(Material.AIR);

        if (source.equals("null")) {
            return itemStack;
        }

        String[] parts = source.split(":");
        for (String part : parts) {
            String[] split = part.split("@");
            String key = split[0];
            String value = split[1];

            switch (key) {
                case "t":
                    itemStack.setType(Material.getMaterial(value));
                    break;
                case "d":
                    itemStack.setDurability(Short.parseShort(value));
                    break;
                case "a":
                    itemStack.setAmount(Integer.parseInt(value));
                    break;
                case "e":
                    Enchantment enchantment = Enchantment.getByName(value);
                    if (enchantment != null) {
                        int level = Integer.parseInt(split[2]);
                        itemStack.addUnsafeEnchantment(enchantment, level);
                    }
                    break;
                case "dn":
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(value);
                        itemStack.setItemMeta(meta);
                    }
                    break;
                case "l":
                    ItemMeta loreMeta = itemStack.getItemMeta();
                    if (loreMeta != null) {
                        List<String> lore = new ArrayList<>();
                        for (int i = 1; i < split.length; i++) {
                            lore.add(split[i]);
                        }
                        loreMeta.setLore(lore);
                        itemStack.setItemMeta(loreMeta);
                    }
                    break;
            }
        }

        return itemStack;
    }

    // Serialize single PotionEffect
    public static String serializeEffect(PotionEffect effect) {
        if (effect == null) {
            return "null";
        }

        return "p@" + effect.getType().getName() + ":d@" + effect.getDuration() + ":a@" + effect.getAmplifier();
    }

    // Deserialize single PotionEffect
    public static PotionEffect deserializeEffect(String source) {
        if (source.equals("null")) {
            return null;
        }

        String[] parts = source.split(":");
        PotionEffectType type = null;
        int duration = 0;
        int amplifier = 0;

        for (String part : parts) {
            String[] split = part.split("@");
            String key = split[0];
            String value = split[1];

            switch (key) {
                case "p":
                    type = PotionEffectType.getByName(value);
                    break;
                case "d":
                    duration = Integer.parseInt(value);
                    break;
                case "a":
                    amplifier = Integer.parseInt(value);
                    break;
            }
        }

        if (type != null) {
            return new PotionEffect(type, duration, amplifier);
        } else {
            return null;
        }
    }

}
