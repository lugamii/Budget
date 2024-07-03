package dev.lugami.practice.task;

import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuTask extends BukkitRunnable {
    public void run() {
        for (Player player : Menu.getOpenMenus().keySet()) {
            Menu menu = Menu.getOpenMenus().get(player);
            menu.open(player);
        }

    }
}
