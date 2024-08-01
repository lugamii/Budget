package dev.lugami.practice.utils.menu;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.*;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutWindowItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;


public class Menu {

    @Getter
    private final String title;
    @Getter
    private final int size;

    @Getter
    private final InventoryWrapper inventory;

    private final Inventory internalInv;

    @Getter
    private final Map<Integer, Button> buttons;
    @Getter
    private static final ItemStack placeholder = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name(" ").build();
    @Getter
    private static final Button placeholderButton = new Button(placeholder);

    @Getter
    private static final Map<Player, Menu> openMenus = new HashMap<>();

    /**
     * Constructs a new Menu with the specified title and size.
     *
     * @param t the title of the menu
     * @param s the size of the menu (must be a multiple of 9)
     */
    public Menu(String t, int s) {
        this.title = CC.translate(t);
        this.size = s;
        this.internalInv = Bukkit.createInventory(null, size, title);
        this.inventory = new InventoryWrapper(internalInv);
        this.buttons = new HashMap<>();
    }

    /**
     * Sets a button at the specified slot in the menu.
     *
     * @param slot the slot to place the button in
     * @param button the button to be placed
     */
    public void setButton(int slot, Button button) {
        if (slot > getSize()) slot = internalInv.getSize() - 1;
        if (buttons.get(slot) != null) buttons.remove(slot);
        if (slot < getSize()) {
            if (internalInv.getItem(slot) != null) inventory.setItem(slot, null);
            inventory.setItem(slot, button.getItemStack());
            buttons.put(slot, button);
        }
    }

    /**
     * Retrieves the button at the specified slot in the menu.
     *
     * @param slot the slot to retrieve the button from
     * @return the button at the specified slot, or null if no button is present
     */
    public Button getButton(int slot) {
        return buttons.get(slot);
    }

    /**
     * Opens the menu for the specified player.
     *
     * @param player the player to open the menu for
     */
    public void open(Player player) {
        if (this.internalInv.getSize() != 0) {
            inventory.clearExcept(placeholder);
            buttons.clear();
        }
        this.initialize(player);
        TaskUtil.runTaskLater(() -> {
            if (!player.hasPermission("budget.menu.bypass") && Budget.getInstance().getProfileStorage().findProfile(player).getState() != ProfileState.LOBBY) {
                player.sendMessage(Language.CANNOT_DO_ACTION.format());
                return;
            }
            if (this.openMenus.get(player) != null) {
                ((CraftPlayer) player).getHandle().p();
                this.openMenus.remove(player);
            }
            player.openInventory(internalInv);
            this.openMenus.put(player, this);
        }, 1);
    }



    /**
     * Handles a click event at the specified slot for the given player.
     *
     * @param slot the slot that was clicked
     * @param player the player who clicked the slot
     */
    public void handleClick(int slot, Player player, ClickType clickType) {
        if (this.buttons.containsKey(slot)) {
            Button button = buttons.get(slot);
            if (button.getAction() != null) button.getAction().execute(player, clickType);
        }
    }

    /**
     * Fills the border of the menu with a placeholder button.
     * The border includes the top and bottom rows, as well as the left and right columns.
     */
    public void fillBorder() {
        if (this.size < 9) return;

        int rows = this.size / 9;

        // Fill top row
        for (int i = 0; i < 9; i++) {
            if (this.getButton(i) == null) {
                this.setButton(i, placeholderButton);
            }
        }

        // Fill bottom row
        for (int i = size - 9; i < size; i++) {
            if (this.getButton(i) == null) {
                this.setButton(i, placeholderButton);
            }
        }

        // Fill left and right columns
        for (int i = 1; i < rows - 1; i++) {
            if (this.getButton(i * 9) == null) {
                this.setButton(i * 9, placeholderButton);
            }
            if (this.getButton(i * 9 + 8) == null) {
                this.setButton(i * 9 + 8, placeholderButton);
            }
        }
    }

    /**
     * Initializes the menu.
     * This method is intended to be overridden by subclasses to add custom initialization logic.
     */
    public void initialize(Player player) {

    }

    /**
     * Updates the lore of all buttons in the menu.
     */
    public void updateButtonLore(Player player) {
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            Button button = entry.getValue();
            ItemStack itemStack = button.getItemStack();
            List<String> newLore = getUpdatedLore(player, entry.getKey(), itemStack);
            if (newLore != null) {
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(newLore);
                itemStack.setItemMeta(meta);
                setButton(entry.getKey(), new Button(itemStack.clone(), button.getAction()));
            }
        }
    }


    /**
     * Override this method in subclasses to provide the updated lore for a button.
     *
     * @param slot the slot of the button
     * @param itemStack the ItemStack of the button
     * @return the updated lore, or null if no update is needed
     */
    public List<String> getUpdatedLore(Player player, int slot, ItemStack itemStack) {
        return null;
    }
}
