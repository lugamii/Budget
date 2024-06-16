package dev.lugami.practice.utils.menu;

import dev.lugami.practice.Budget;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MenuManager {

    @Getter
    private static final List<Menu> menus = new ArrayList<>();

    public void addMenu(Menu menu) {
        if (!menus.contains(menu)) {
            menus.add(menu);
        } else {
            Bukkit.getLogger().warning("[Budget] Tried adding a menu (Title: " + menu.getTitle() + "), but it already existed, returning...");
        }
    }

}
