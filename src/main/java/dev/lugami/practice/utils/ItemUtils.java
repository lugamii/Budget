package dev.lugami.practice.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    public static String serializeInventory(ItemStack[] source) {
        StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : source) {
            builder.append(serializeItemStack(itemStack));
            builder.append(";");
        }

        return builder.toString();
    }

    public static ItemStack[] deserializeInventory(String source) {
        List<ItemStack> items = new ArrayList<>();
        String[] split = source.split(";");

        for (String piece : split) {
            items.add(deserializeItemStack(piece));
        }

        return items.toArray(new ItemStack[items.size()]);
    }

    public static String serializeEffects(List<PotionEffect> source) {
        StringBuilder builder = new StringBuilder();
        if (source.size() == 0) return null;

        for (PotionEffect potionEffect : source) {
            String potionString = serializeEffect(potionEffect);
            if (potionString == null || potionString == "null") continue;

            builder.append(potionString);
            builder.append(";");
        }

        return builder.toString();
    }

    public static List<PotionEffect> deserializeEffects(String source) {
        List<PotionEffect> items = new ArrayList<>();

        if (source.equalsIgnoreCase(""))
            return null;

        String[] split = source.split(";");

        for (String piece : split) {
            items.add(deserializeEffect(piece));
        }

        return items;
    }

    public static String serializeItemStack(ItemStack item) {
        StringBuilder builder = new StringBuilder();

        if (item == null) {
            return "null";
        }

        String isType = String.valueOf(item.getType().getId());
        builder.append("t@").append(isType);

        if (item.getDurability() != 0) {
            String isDurability = String.valueOf(item.getDurability());
            builder.append(":d@").append(isDurability);
        }

        if (item.getAmount() != 1) {
            String isAmount = String.valueOf(item.getAmount());
            builder.append(":a@").append(isAmount);
        }

        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        if (enchantments.size() > 0) {
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                builder.append(":e@").append(enchantment.getKey().getId()).append("@").append(enchantment.getValue());
            }
        }

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.hasDisplayName()) {
                builder.append(":dn@").append(itemMeta.getDisplayName());
            }

            if (itemMeta.hasLore()) {
                builder.append(":l@").append(itemMeta.getLore());
            }
        }

        return builder.toString();
    }

    public static ItemStack deserializeItemStack(String in) {
        ItemStack item = null;
        ItemMeta meta = null;

        if (in.equals("null")) {
            return new ItemStack(Material.AIR);
        }

        String[] split = in.split(":");

        for (String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String attributeId = itemAttribute[0];

            switch (attributeId) {
                case "t": {
                    try {
                        item = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                        meta = item.getItemMeta();
                    } catch (NumberFormatException exception) {
                        item = new ItemStack(Material.getMaterial(itemAttribute[1]));
                        meta = item.getItemMeta();
                    }
                    break;
                }
                case "d": {
                    if (item != null) {
                        item.setDurability(Short.valueOf(itemAttribute[1]));
                        break;
                    }
                    break;
                }
                case "a": {
                    if (item != null) {
                        item.setAmount(Integer.valueOf(itemAttribute[1]));
                        break;
                    }
                    break;
                }
                case "e": {
                    if (item != null) {
                        item.addUnsafeEnchantment(
                                Enchantment.getById(Integer.valueOf(itemAttribute[1])),
                                Integer.valueOf(itemAttribute[2])
                        );
                        break;
                    }
                    break;
                }
                case "dn": {
                    if (meta != null) {
                        meta.setDisplayName(itemAttribute[1]);
                        break;
                    }
                    break;
                }
                case "l": {
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");
                    List<String> lore = Arrays.asList(itemAttribute[1].split(","));

                    for (int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);

                        if (s != null) {
                            if (s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }

                                lore.set(x, s);
                            }
                        }
                    }

                    if (meta != null) {
                        meta.setLore(lore);
                        break;
                    }

                    break;
                }
            }
        }

        if (meta != null && (meta.hasDisplayName() || meta.hasLore())) {
            item.setItemMeta(meta);
        }

        return item;
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

    // Credits: Praxi (joeleoli)
    public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
        ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

}
