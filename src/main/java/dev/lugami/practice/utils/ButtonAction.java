package dev.lugami.practice.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface ButtonAction {

    void execute(Player player, ClickType clickType);

}
