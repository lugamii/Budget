package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.menus.editor.EditorMenu;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.InventoryWrapper;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EditorListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (!event.getAction().name().contains("RIGHT_")) {
            return;
        }
        if (profile.getState() == ProfileState.EDITOR) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || !(clickedBlock.getType() == Material.SIGN_POST || clickedBlock.getType() == Material.WALL_SIGN || clickedBlock.getType() == Material.ANVIL || clickedBlock.getType() == Material.CHEST)) {
                return;
            }

            if (clickedBlock.getType() == Material.ANVIL) {
                if (profile.getState() == ProfileState.EDITOR && profile.getEditingMetadata() != null) {
                    event.setCancelled(true);
                    new EditorMenu(profile.getEditingMetadata().getEditing()).open(player);
                }
                return;
            }

            if (clickedBlock.getType() == Material.CHEST) {
                event.setCancelled(true);
                InventoryWrapper inventoryWrapper = new InventoryWrapper(Bukkit.createInventory(player, 36));
                if (profile.getState() == ProfileState.EDITOR && profile.getEditingMetadata() != null) {
                    int i = 0;
                    for (ItemStack itemStack : profile.getEditingMetadata().getEditing().getInventory()) {
                        inventoryWrapper.setItem(i++, itemStack);
                    }
                }
                player.openInventory(inventoryWrapper.get());
                return;
            }

            if (profile.getEditingMetadata() != null) {
                event.setCancelled(true);
                Budget.getInstance().getLobbyStorage().bringToLobby(player);
            }
        }
    }

}
