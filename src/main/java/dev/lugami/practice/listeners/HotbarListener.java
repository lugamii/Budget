package dev.lugami.practice.listeners;

import dev.lugami.practice.Budget;
import dev.lugami.practice.hotbar.HotbarItem;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.InventoryWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HotbarListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (event.getItem() == null) return;
        if (profile.getState() == ProfileState.FIGHTING) return;
        Budget.getInstance().getHotbarStorage().getByState(profile.getState()).forEach(hotbarItem -> {
            if (event.getItem().isSimilar(hotbarItem.getItemStack()) && event.getAction().name().contains("RIGHT_")) {
                hotbarItem.getAction().execute(player);
            }
        });

    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.FIGHTING) return;
        Budget.getInstance().getHotbarStorage().getByState(profile.getState()).forEach(hotbarItem -> {
            if (event.getItemDrop().getItemStack().isSimilar(hotbarItem.getItemStack())) {
                event.setCancelled(true);
            }
        });

    }

    @EventHandler
    public void onPlayerInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) {
            return;
        }
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);

        if (profile.getState() == ProfileState.FIGHTING) return;
        Budget.getInstance().getHotbarStorage().getByState(profile.getState()).forEach(hotbarItem -> {
            if (event.getCurrentItem().isSimilar(hotbarItem.getItemStack())) {
                event.setCancelled(true);
            }
        });
    }

}
