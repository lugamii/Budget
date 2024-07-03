package dev.lugami.practice.utils.menu;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.InventoryWrapper;
import dev.lugami.practice.utils.ItemBuilder;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class Menu {

    private final String title;
    private final int size;
    private final Inventory inventory;
    private final Map<Integer, Button> buttons;
    private final ItemStack placeholder = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name(" ").build();
    private final Button placeholderButton = new Button(placeholder);

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
        this.inventory = Bukkit.createInventory(null, size, title);
        this.buttons = new HashMap<>();
    }

    /**
     * Sets a button at the specified slot in the menu.
     *
     * @param slot the slot to place the button in
     * @param button the button to be placed
     */
    public void setButton(int slot, Button button) {
        buttons.put(slot, button);
        inventory.setItem(slot, button.getItemStack());
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
        if (this.getInventory().getSize() != 0) {
            InventoryWrapper wrapper = new InventoryWrapper(this.getInventory());
            wrapper.clear();
            buttons.clear();
        }
        this.initialize();
        TaskUtil.runTaskLater(() -> {
            if (!player.hasPermission("budget.menu.bypass") && Budget.getInstance().getProfileStorage().findProfile(player).getState() != ProfileState.LOBBY) {
                player.sendMessage(Language.CANNOT_DO_ACTION.format());
                return;
            }
            if (player.getOpenInventory() != null) {
                ((CraftPlayer) player).getHandle().p();
                openMenus.remove(player);
            }
            player.openInventory(inventory);
            openMenus.put(player, this);
        }, 1);
    }



    /**
     * Handles a click event at the specified slot for the given player.
     *
     * @param slot the slot that was clicked
     * @param player the player who clicked the slot
     */
    public void handleClick(int slot, Player player) {
        if (buttons.containsKey(slot)) {
            Button button = buttons.get(slot);
            button.getAction().execute(player);
        }
    }

    /**
     * Fills the border of the menu with a placeholder button.
     * The border includes the top and bottom rows, as well as the left and right columns.
     */
    public void fillBorder() {
        if (size < 9) return;

        int rows = size / 9;

        // Fill top row
        for (int i = 0; i < 9; i++) {
            if (getButton(i) == null) {
                setButton(i, placeholderButton);
            }
        }

        // Fill bottom row
        for (int i = size - 9; i < size; i++) {
            if (getButton(i) == null) {
                setButton(i, placeholderButton);
            }
        }

        // Fill left and right columns
        for (int i = 1; i < rows - 1; i++) {
            if (getButton(i * 9) == null) {
                setButton(i * 9, placeholderButton);
            }
            if (getButton(i * 9 + 8) == null) {
                setButton(i * 9 + 8, placeholderButton);
            }
        }
    }

    /**
     * Initializes the menu.
     * This method is intended to be overridden by subclasses to add custom initialization logic.
     */
    public void initialize() {

    }
}
