package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.utils.menu.Menu;
import dev.lugami.practice.utils.menu.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class MenuListener implements Listener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
            return;
        }

        event.setCancelled(true); // Prevent item moving

        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }

        String title = inventory.getTitle();
        for (Menu menu : MenuManager.getMenus()) {
            if (menu.getTitle().equals(title)) {
                menu.handleClick(event.getSlot(), player);
                return;
            }
        }
    }

}
