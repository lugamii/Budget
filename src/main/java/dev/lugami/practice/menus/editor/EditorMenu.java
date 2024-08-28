package dev.lugami.practice.menus.editor;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.editor.CustomKitLayout;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public class EditorMenu extends Menu {

    private final Kit kit;

    /**
     * Constructs a new Menu with the specified title and size.
     */
    public EditorMenu(Kit kit) {
        super("&bEditing kits", 9);
        this.kit = kit;
    }

    @Override
    public void initialize(Player player) {
        setButton(1, getKitButton(player, 1));
        setButton(3, getKitButton(player, 2));
        setButton(5, getKitButton(player, 3));
        setButton(7, getKitButton(player, 4));
    }

    private Button getKitButton(Player player, int id) {
        if (id <= 0) id = 1;
        int finalId = id;
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        CustomKitLayout[] kitLayouts = profile.getKitLayouts().get(kit);
        if (kitLayouts == null) {
            kitLayouts = new CustomKitLayout[4];
        }
        CustomKitLayout savedKitLayout;
        try {
            savedKitLayout = kitLayouts[finalId - 1];
        } catch (Exception e) {
            savedKitLayout = null;
        }
        List<String> lore = new ArrayList<>();
        lore.add(CC.translate(""));
        if (savedKitLayout == null || savedKitLayout.getArmor() == kit.getArmor() && savedKitLayout.getInventory() == kit.getInventory()) {
            lore.add("&cNo saved kit.");
            lore.add("");
        }
        lore.add(CC.translate("&eLeft-Click to save this kit."));
        lore.add(CC.translate("&eMiddle-Click to delete this kit."));
        lore.add(CC.translate("&eRight-Click to load this kit."));
        CustomKitLayout finalSavedKitLayout = savedKitLayout;
        CustomKitLayout[] finalKitLayouts = kitLayouts;
        return new Button(new ItemBuilder(Material.ENCHANTED_BOOK).name("&bKit #" + id).lore(lore).build(), (p1, clickType) -> {
            if (clickType == ClickType.LEFT) {
                if ((finalSavedKitLayout == null || !areInventoriesEqual(finalSavedKitLayout, p1)) && !isInventoryEmpty(p1)) {
                    CustomKitLayout newKitLayout = new CustomKitLayout();
                    newKitLayout.setArmor(p1.getInventory().getArmorContents());
                    newKitLayout.setInventory(p1.getInventory().getContents());
                    finalKitLayouts[finalId - 1] = newKitLayout;
                    profile.getKitLayouts().put(kit, finalKitLayouts);
                    p1.sendMessage(CC.translate("&aSaved the kit " + finalId +  "!"));
                }
            } else if (clickType == ClickType.RIGHT) {
                if (finalSavedKitLayout != null) {
                    p1.getInventory().setArmorContents(finalSavedKitLayout.getArmor());
                    p1.getInventory().setContents(finalSavedKitLayout.getInventory());
                    p1.updateInventory();
                    p1.sendMessage(CC.translate("&aLoaded the kit " + finalId +  "!"));
                } else {
                    p1.sendMessage(CC.translate("&cYou don't have a kit saved on this index."));
                }
            } else if (clickType == ClickType.MIDDLE) {
                if (finalSavedKitLayout != null) {
                    finalKitLayouts[finalId - 1] = null;
                    profile.getKitLayouts().put(kit, finalKitLayouts);
                    p1.sendMessage(CC.translate("&cDeleted the kit " + finalId + "."));
                }
            }
        });
    }

    private boolean areInventoriesEqual(CustomKitLayout savedLayout, Player player) {
        ItemStack[] savedArmor = savedLayout.getArmor();
        ItemStack[] playerArmor = player.getInventory().getArmorContents();
        if (!Arrays.equals(savedArmor, playerArmor)) {
            return false;
        }
        ItemStack[] savedInventory = savedLayout.getInventory();
        ItemStack[] playerInventory = player.getInventory().getContents();
        return Arrays.equals(savedInventory, playerInventory);
    }

    private boolean isInventoryEmpty(Player player) {
        return player.getInventory().firstEmpty() == 0;
    }

}
