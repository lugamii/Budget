package dev.lugami.practice.task;

import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            if (Menu.getOpenMenus().containsKey(p1)) {
                Menu menu = Menu.getOpenMenus().get(p1);
                menu.updateButtonLore(p1);
                menu.initialize(p1);
                p1.updateInventory();
            }
        }
    }
}
