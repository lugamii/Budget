package dev.lugami.practice.listeners;

import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

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

        event.setCancelled(true);

        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }

        String title = inventory.getTitle();
        Menu menu = Menu.getOpenMenus().get(player);
        if (menu.getTitle().equals(title)) {
            menu.handleClick(event.getSlot(), player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (Menu.getOpenMenus().get(player) != null) {
            Menu.getOpenMenus().remove(player);
        }
    }

}
